package com.example.tft.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tft.ui.ChangePassword.ChangePasswordScreen
import com.example.tft.ui.EditProfile.EditProfileScreen
import com.example.tft.ui.PilotFavorites.PilotFavoritesScreen
import com.example.tft.ui.SeasonDetail.SeasonDetailScreen
import com.example.tft.ui.StageDetail.StageDetailScreen
import com.example.tft.ui.StageDetailDay.StageDaysDetailScreen
import com.example.tft.ui.allEventsScreen.AllEventsScreen
import com.example.tft.ui.calendar.CalendarScreen
import com.example.tft.ui.car.CarScreen
import com.example.tft.ui.carCategories.CarCategoriesScreen
import com.example.tft.ui.community.CommunityScreen
import com.example.tft.ui.createQuestion.CreateQuestionScreen
import com.example.tft.ui.createQuestion.CreateQuestionViewModel
import com.example.tft.ui.eventDetails.EventDetailScreen
import com.example.tft.ui.events.EventScreen
import com.example.tft.ui.Question_And_Report.FaqScreen
import com.example.tft.ui.filterEvent.FilterEventScreen
import com.example.tft.ui.forgotpassword.ForgotPasswordScreen
import com.example.tft.ui.globalClassification.GlobalClassificationScreen
import com.example.tft.ui.home.HomeScreen
import com.example.tft.ui.login.LoginScreen
import com.example.tft.ui.news.NewsScreen
import com.example.tft.ui.newsDetail.NewsDetailScreen
import com.example.tft.ui.notification.NotificationScreen
import com.example.tft.ui.other.OtherScreen
import com.example.tft.ui.padock.PadockScreen
import com.example.tft.ui.photos.PhotosScreen
import com.example.tft.ui.pilotDetail.PilotDetailScreen
import com.example.tft.ui.pilots.PilotScreen
import com.example.tft.ui.questionsDetail.QuestionsDetailScreen
import com.example.tft.ui.register.RegisterScreen
import com.example.tft.ui.season.SeasonScreen
import com.example.tft.ui.sectionsSeason.SectionsSeasonScreen
import com.example.tft.ui.sectionsStage.SectionsStageScreen
import com.example.tft.ui.settings.SettingScreen
import com.example.tft.ui.userProfile.UserProfileScreen
import com.example.tft.ui.videos.VideoScreen
import com.example.tft.ui.votationDetail.VotationDetailScreen
import com.example.tft.ui.Question_And_Report.Question_And_ReportViewModel
import com.example.tft.ui.settings.SettingViewModel
import com.example.tft.ui.teamWrc.TeamWrcScreen
import com.example.tft.ui.teamWrc.TeamWrcViewModel
import com.example.tft.ui.votationDetail.VotationDetailViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(navController)
        }

        composable(route = AppScreens.RegisterScreen.route) {
            RegisterScreen(navController)
        }

        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(navController)
        }

        composable(route = AppScreens.UserProfileScreen.route) {
            UserProfileScreen(navController)
        }

        composable(route = AppScreens.EventScreen.route) {
            EventScreen(navController)
        }

        composable(route = AppScreens.NewsScreen.route) {
            NewsScreen(navController)
        }

        composable(
            route = AppScreens.VotationDetailScreen.route,
            arguments = listOf(navArgument("votationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val votationId = backStackEntry.arguments?.getString("votationId")
            votationId?.let {
                val viewModel: VotationDetailViewModel = viewModel()
                VotationDetailScreen(navController, it, viewModel)
            }
        }

        composable(
            route = AppScreens.NewsDetailScreen.route,
            arguments = listOf(navArgument("newsId") { type = NavType.StringType })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
            NewsDetailScreen(navController, newsId)
        }

        composable(route = AppScreens.PilotScreen.route) {
            PilotScreen( navController = navController)
        }

        composable(route = AppScreens.PadockScreen.route) {
            PadockScreen( navController = navController)
        }

        composable(
            route = AppScreens.QuestionsDetailScreen.route,
            arguments = listOf(navArgument("questionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getString("questionId")
            questionId?.let { QuestionsDetailScreen(navController, it) }
        }

        composable(route = AppScreens.PhotosScreen.route) {
            PhotosScreen( navController = navController)
        }

        composable(route = AppScreens.AllEventsScreen.route) {
            AllEventsScreen( navController = navController)
        }

        composable(
            route = AppScreens.FilterEventScreen.route + "/{level}",
            arguments = listOf(navArgument("level") { type = NavType.StringType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level") ?: return@composable
            FilterEventScreen(navController, level)
        }

        composable(route = AppScreens.CommunityScreen.route) {
            CommunityScreen( navController = navController)
        }

        composable(route = AppScreens.UserProfileScreen.route) {
            UserProfileScreen(navController)
        }

        composable(route = AppScreens.CalendarScreen.route) {
            CalendarScreen(navController)
        }

        composable(route = AppScreens.CarCategoriesScreen.route) {
            CarCategoriesScreen(navController)
        }

        composable(route = AppScreens.CreateQuestionScreen.route) {
            val viewModel: CreateQuestionViewModel = viewModel()
            CreateQuestionScreen(navController = navController, viewModel = viewModel)
        }

        composable(route = AppScreens.TeamWrcScreen.route) {
            val viewModel: TeamWrcViewModel = viewModel()
            TeamWrcScreen(navController = navController, viewModel = viewModel)
        }

        composable(route = AppScreens.VideoScreen.route) {
            VideoScreen(navController)
        }

        composable(route = AppScreens.OtherScreen.route) {
            OtherScreen(navController)
        }

        composable(route = AppScreens.NotificationScreen.route) {
            NotificationScreen(navController)
        }

        composable(
            route = AppScreens.CarScreen.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            CarScreen(navController, categoryId)
        }

        composable(route = AppScreens.SettingScreen.route) {
            val settingViewModel: SettingViewModel = viewModel()  // Obtiene ViewModel aquí
            SettingScreen(navController, settingViewModel)  // Pasa el ViewModel
        }

        composable(route = AppScreens.PilotFavoritesScreen.route) {
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid
            if (userId != null) {
                PilotFavoritesScreen(userId, navController)
            } else {
            }
        }

        composable(route = AppScreens.FaqScreen.route) {
            val viewModel: Question_And_ReportViewModel = viewModel()
            FaqScreen(navController,viewModel)
        }

        composable(route = AppScreens.SeasonScreen.route) {
            SeasonScreen(navController)
        }

        composable(route = AppScreens.EditProfileScreen.route) {
            EditProfileScreen(navController)
        }

        composable(route = AppScreens.GlobalClassificationScreen.route + "/{seasonId}") { backStackEntry ->
            val seasonId = backStackEntry.arguments?.getString("seasonId") ?: return@composable
            GlobalClassificationScreen(navController, seasonId)
        }

        composable(route = AppScreens.SectionsSeasonScreen.route + "/{seasonId}") { backStackEntry ->
            val seasonId = backStackEntry.arguments?.getString("seasonId") ?: return@composable
            SectionsSeasonScreen(navController = navController, seasonId = seasonId)
        }

        composable(route = AppScreens.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navController)
        }

        composable(route = AppScreens.SeasonDetailScreen.route + "/{seasonId}") { backStackEntry ->
            val seasonId = backStackEntry.arguments?.getString("seasonId") ?: return@composable
            SeasonDetailScreen(navController = navController, seasonId = seasonId)
        }

        composable(route = AppScreens.StageDetailScreen.route + "/{stageId}") { backStackEntry ->
            val stageId = backStackEntry.arguments?.getString("stageId") ?: return@composable
            StageDetailScreen(navController = navController, stageId = stageId)
        }

        composable(route = AppScreens.StageDaysDetailScreen.route + "/{stageId}") { backStackEntry ->
            val stageId = backStackEntry.arguments?.getString("stageId") ?: return@composable
            StageDaysDetailScreen(navController = navController, stageId = stageId)
        }

        composable(route = AppScreens.SectionsStageScreen.route + "/{stageId}") { backStackEntry ->
            val stageId = backStackEntry.arguments?.getString("stageId") ?: return@composable
            SectionsStageScreen(navController = navController, stageId = stageId)
        }

        composable(
            route = AppScreens.PilotDetailScreen.route,
            arguments = listOf(navArgument("pilotId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pilotId = backStackEntry.arguments?.getInt("pilotId") ?: return@composable
            PilotDetailScreen(pilotId = pilotId, navController = navController)
        }

        composable(AppScreens.EventDetailScreen.route + "/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            if (eventId != null) {
                EventDetailScreen(navController, eventId)
            }
        }

        composable(AppScreens.ChangePasswordScreen.route) {
            ChangePasswordScreen(navController = navController)
        }

    }
}
