package br.eti.rafaelcouto.gymbro.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.eti.rafaelcouto.gymbro.R
import br.eti.rafaelcouto.gymbro.domain.model.Exercise
import br.eti.rafaelcouto.gymbro.presentation.components.Checkbox
import br.eti.rafaelcouto.gymbro.presentation.components.DropDownMenu
import br.eti.rafaelcouto.gymbro.presentation.components.EmptyMessage
import br.eti.rafaelcouto.gymbro.presentation.components.FloatingActionButton
import br.eti.rafaelcouto.gymbro.presentation.components.RoundedButton
import br.eti.rafaelcouto.gymbro.presentation.uistate.ExerciseListUiState
import br.eti.rafaelcouto.gymbro.presentation.uistate.MainActivityUiState

@Composable
fun ExerciseListScreen(
    onIncreaseLoad: (Exercise) -> Unit = {},
    onDecreaseLoad: (Exercise) -> Unit = {},
    onSetFinshed: (exercise: Exercise.UI, set: Int) -> Unit = { _, _ -> },
    onEditExerciseClick: (Exercise) -> Unit = {},
    onAddExercise: (Long) -> Unit = {},
    onFinishWorkout: () -> Unit = {},
    onMenuToggle: (Boolean) -> Unit = {},
    onEditWorkoutClick: (Long) -> Unit = {},
    onDeleteWorkoutClicked: () -> Unit = {},
    showMessage: (String) -> Unit = {},
    setMainActivityState: (MainActivityUiState) -> Unit = {},
    state: ExerciseListUiState = ExerciseListUiState()
) {

    val exerciseFinishedSuccessMessage = stringResource(id = R.string.exercise_done)
    val decreaseLoadSuccessMessage = stringResource(id = R.string.load_decreased)
    val increaseLoadSuccessMessage = stringResource(id = R.string.load_increased)
    val workoutDeletedSuccessMessage = stringResource(id = R.string.workout_deleted)
    val workoutFinishedSuccessMessage = stringResource(id = R.string.workout_finished)

    if (state.shouldDisplayEmptyMessage)
        EmptyMessage(
            text = stringResource(id = R.string.no_exercises_found)
        )

    ExerciseList(
        exercises = state.exercises,
        onIncreaseLoad = {
            onIncreaseLoad(it)
            showMessage(increaseLoadSuccessMessage)
        },
        onDecreaseLoad = {
            onDecreaseLoad(it)
            showMessage(decreaseLoadSuccessMessage)
        },
        onEditExerciseClick = onEditExerciseClick,
        onExerciseFinished = {
            showMessage(exerciseFinishedSuccessMessage)
        },
        onSetFinished = onSetFinshed,
        onWorkoutFinished = {
            showMessage(workoutFinishedSuccessMessage)
        },
        canFinishWorkout = state.canFinishWorkout
    )

    LaunchedEffect(
        state.workout,
        state.canFinishWorkout,
        state.isMenuExpanded,
        state.shouldDisplayEmptyMessage,
        block = {
            val mainState = MainActivityUiState(
                title = state.workout.name,
                showsBackButton = true,
                floatingActionButton = {
                    FloatingActionButton(
                        icon = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_exercise),
                        onClick = {
                            onAddExercise(state.workout.id)
                        }
                    )
                },
                topAppBarActions = {
                    if (!state.shouldDisplayEmptyMessage) {
                        IconButton(
                            enabled = state.canFinishWorkout,
                            onClick = onFinishWorkout,
                            content = {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = stringResource(id = R.string.finish_workout)
                                )
                            }
                        )
                    }

                    DropDownMenu(
                        expanded = state.isMenuExpanded,
                        onToggle = onMenuToggle
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.edit)) },
                            onClick = {
                                onEditWorkoutClick(state.workout.id)
                                onMenuToggle(false)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.delete)) },
                            onClick = {
                                onDeleteWorkoutClicked()
                                showMessage(workoutDeletedSuccessMessage)
                            }
                        )
                    }
                }
            )
            setMainActivityState(mainState)
        }
    )
}

@Composable
fun ExerciseList(
    exercises: List<Exercise.UI> = emptyList(),
    onIncreaseLoad: (Exercise) -> Unit = {},
    onDecreaseLoad: (Exercise) -> Unit = {},
    onEditExerciseClick: (Exercise) -> Unit = {},
    onExerciseFinished: () -> Unit = {},
    onSetFinished: (exercise: Exercise.UI, set: Int) -> Unit = { _, _ -> },
    onWorkoutFinished: () -> Unit = {},
    canFinishWorkout: Boolean = true
) {
    LaunchedEffect(key1 = canFinishWorkout) {
        if (!canFinishWorkout)
            onWorkoutFinished()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        content = {
            items(exercises) { exercise ->
                ExerciseItem(
                    exercise = exercise,
                    onIncreaseLoad = onIncreaseLoad,
                    onDecreaseLoad = onDecreaseLoad,
                    onEditExerciseClick = onEditExerciseClick,
                    onExerciseFinished = onExerciseFinished,
                    onSetFinished = onSetFinished
                )
            }
        }
    )
}

@Composable
fun ExerciseItem(
    exercise: Exercise.UI,
    onIncreaseLoad: (Exercise) -> Unit = {},
    onDecreaseLoad: (Exercise) -> Unit = {},
    onEditExerciseClick: (Exercise) -> Unit = {},
    onExerciseFinished: () -> Unit = {},
    onSetFinished: (exercise: Exercise.UI, set: Int) -> Unit = { _, _ ->}
) {

    LaunchedEffect(key1 = exercise.finished) {
        if (exercise.finished)
            onExerciseFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_m),
                vertical = dimensionResource(id = R.dimen.padding_p)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_xp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = exercise.original.name
            )
            RoundedButton(
                onClick = {
                    onEditExerciseClick(exercise.original)
                },
                icon = Icons.Filled.Edit,
                contentDescription = stringResource(id = R.string.edit_exercise)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_p)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val setsAndReps = if (exercise.isSameNumberOfReps)
                stringResource(
                    id = R.string.sets_and_reps,
                    exercise.original.sets,
                    exercise.original.minReps
                )
            else
                stringResource(
                    id = R.string.sets_min_max_reps,
                    exercise.original.sets,
                    exercise.original.minReps,
                    exercise.original.maxReps
                )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = setsAndReps
            )
            RoundedButton(
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_m)),
                onClick = {
                    onDecreaseLoad(exercise.original)
                },
                icon = Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(id = R.string.decrease_load)
            )
            Text(
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_m)),
                text = stringResource(id = R.string.load_kg, exercise.original.load)
            )
            RoundedButton(
                onClick = {
                    onIncreaseLoad(exercise.original)
                },
                icon = Icons.Filled.KeyboardArrowUp,
                contentDescription = stringResource(id = R.string.increase_weight)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_p)),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = stringResource(id = R.string.sets_done)
                )
                exercise.setsState.forEachIndexed { index, value ->
                    Checkbox(
                        modifier = Modifier.padding(
                            start = if (index == 0) 0.dp else dimensionResource(id = R.dimen.padding_p)
                        ),
                        checked = value,
                        onCheckedChange = {
                            onSetFinished(exercise, index)
                        },
                        enabled = !exercise.finished
                    )
                }
            }
        )
    }
    HorizontalDivider(color = colorResource(id = R.color.colorPrimaryAlpha))
}

@Preview(showSystemUi = true)
@Composable
private fun ExerciseListScreenEmptyPreview() {
    ExerciseListScreen()
}

@Preview(showSystemUi = true)
@Composable
private fun ExerciseListScreenNotEmptyPreview() {
    ExerciseListScreen(
        state = ExerciseListUiState(
            exercises = listOf(
                Exercise.UI(
                    original = Exercise(
                        name = "Exercise 1",
                        sets = 3,
                        minReps = 8,
                        maxReps = 12,
                        load = 50,
                        workoutId = 1
                    )
                ),
                Exercise.UI(
                    original = Exercise(
                        name = "Exercise 2",
                        sets = 4,
                        minReps = 10,
                        maxReps = 10,
                        load = 20,
                        workoutId = 1
                    ),
                    setsState = listOf(true, true, false, false)
                ),
                Exercise.UI(
                    original = Exercise(
                        name = "Exercise 3",
                        sets = 3,
                        minReps = 8,
                        maxReps = 12,
                        load = 50,
                        workoutId = 1
                    ),
                    setsState = listOf(true, true, true)
                )
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExerciseListPreview() {
    ExerciseList(
        exercises = listOf(
            Exercise.UI(
                original = Exercise(
                    name = "Exercise 1",
                    sets = 3,
                    minReps = 8,
                    maxReps = 12,
                    load = 50,
                    workoutId = 1
                )
            ),
            Exercise.UI(
                original = Exercise(
                    name = "Exercise 2",
                    sets = 4,
                    minReps = 10,
                    maxReps = 10,
                    load = 20,
                    workoutId = 1
                ),
                setsState = listOf(true, true, false, false)
            ),
            Exercise.UI(
                original = Exercise(
                    name = "Exercise 3",
                    sets = 3,
                    minReps = 8,
                    maxReps = 12,
                    load = 50,
                    workoutId = 1
                ),
                setsState = listOf(true, true, true)
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExerciseItemDefaultPreview() {
    ExerciseItem(
        exercise = Exercise.UI(
            original = Exercise(
                name = "Exercise 2",
                sets = 3,
                minReps = 8,
                maxReps = 12,
                load = 50,
                workoutId = 1
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExerciseItemPartiallyDonePreview() {
    ExerciseItem(
        exercise = Exercise.UI(
            original = Exercise(
                name = "Exercise 2",
                sets = 3,
                minReps = 8,
                maxReps = 12,
                load = 50,
                workoutId = 1
            ),
            setsState = listOf(true, false, false)
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExerciseItemDonePreview() {
    ExerciseItem(
        exercise = Exercise.UI(
            original = Exercise(
                name = "Exercise 2",
                sets = 3,
                minReps = 10,
                maxReps = 10,
                load = 50,
                workoutId = 1
            ),
            setsState = listOf(true, true, true)
        )
    )
}
