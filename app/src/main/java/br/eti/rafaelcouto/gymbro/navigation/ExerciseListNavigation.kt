package br.eti.rafaelcouto.gymbro.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.eti.rafaelcouto.gymbro.presentation.screens.ExerciseListScreen
import br.eti.rafaelcouto.gymbro.presentation.uistate.MainActivityUiState
import br.eti.rafaelcouto.gymbro.presentation.viewmodel.ExerciseListViewModel

const val exerciseListRoute = "exercises"
private const val exerciseListFullRoute = "$workoutFormRoute/{$workoutIdArg}/$exerciseListRoute"

fun NavGraphBuilder.exerciseListScreen(
    navController: NavController,
    setMainActivityState: (MainActivityUiState) -> Unit = {},
    showMessage: (String) -> Unit = {}
) {
    composable(
        route = exerciseListFullRoute,
        arguments = listOf(
            navArgument(workoutIdArg) {
                type = NavType.LongType
                defaultValue = 0L
            }
        )
    ) {
        val viewModel: ExerciseListViewModel = hiltViewModel()
        val state by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) { viewModel.loadContent() }

        ExerciseListScreen(
            onIncreaseLoad = viewModel::increaseLoad,
            onDecreaseLoad = viewModel::decreaseLoad,
            onSetFinshed = viewModel::finishSet,
            onEditExerciseClick = { exercise ->
                navController.navigateToExerciseForm(
                    workoutId = exercise.workoutId,
                    exerciseId = exercise.id
                )
            },
            onAddExercise = navController::navigateToExerciseForm,
            onFinishWorkout = viewModel::finishWorkout,
            onMenuToggle = viewModel::setMenuState,
            onEditWorkoutClick = navController::navigateToWorkoutForm,
            onDeleteWorkoutClicked = {
                viewModel.deleteWorkout()
                navController.popBackStack()
            },
            showMessage = showMessage,
            setMainActivityState = setMainActivityState,
            state = state
        )
    }
}

private fun NavController.navigateToExerciseForm(workoutId: Long) {
    navigate("$workoutFormRoute/$workoutId/$exerciseFormRoute")
}

private fun NavController.navigateToExerciseForm(workoutId: Long, exerciseId: Long) {
    navigate("$workoutFormRoute/$workoutId/$exerciseFormRoute?$exerciseIdArg=$exerciseId")
}
