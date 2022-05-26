package ch.epfl.sdp.mobile.ui.i18n

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/** Localized strings for the English language. */
object French : LocalizedStrings {
  override val authenticateTitle = "Pawnies"
  override val authenticateSubtitleRegister = "Rejoignez la communauté Pawnies.".uppercase()
  override val authenticateSubtitleLogIn = "Bienvenue à nouveau sur Pawnies.".uppercase()
  override val authenticateEmailHint = "Ton email"
  override val authenticateNameHint = "Ton nom complet"
  override val authenticatePasswordHint = "Mot de passe"
  override val authenticatePasswordToggleVisibility = "Toggle la visibilité du mot de passe"
  override val authenticatePerformLogIn = "Se connecter avec l'email".uppercase()
  override val authenticatePerformRegister = "S'inscrire avec l'email".uppercase()
  override val authenticateSwitchToLogIn = "Connectez-toi".uppercase()
  override val authenticateSwitchToRegister = "Inscrivez-toi".uppercase()
  override val authenticateErrorFailure = "Un problème est survenu lors de l'authentification"
  override val authenticateBadPasswordFailure =
      "Ton mot de passe doit comporter au moins 6 caractères"
  override val authenticateWrongEmailFormatFailure =
      "Ton email doit être de la forme : person@domain"
  override val authenticateInvalidUserFailure =
      "Il n'y a pas d'utilisateur enregistré avec cet email"
  override val authenticateIncorrectPasswordFailure =
      "L'adresse électronique ou le mot de passe que vous avez saisi est incorrect"
  override val authenticateExistingAccountFailure =
      "Un utilisateur avec cette adresse e-mail existe déjà"
  override val authenticateOr = "ou"

  override val arContentDescription: String = "ArComposable"

  override val boardColorBlack = "noir"
  override val boardColorWhite = "blanc"
  override val boardPieceKing = "roi"
  override val boardPieceQueen = "dame"
  override val boardPieceRook = "tour"
  override val boardPieceBishop = "fou"
  override val boardPieceKnight = "cavalier"
  override val boardPiecePawn = "pion"
  override val boardPieceContentDescription = { color: String, rank: String -> "$color $rank" }
  override val boardContentDescription = "échiquier"

  override val gameBack = "Retour"
  override val gameShowAr = "Lancer le mode AR"
  override val gameMicOffContentDescription = "Microphone désactivé"
  override val gameMicOnContentDescription = "Microphone activé"
  override val gameListening = "Écoute"
  override val gameMessageYourTurn = "Ton tour"
  override val gameMessageCheck = "Échec !"
  override val gameMessageStalemate = "Impasse !"
  override val gameMessageCheckmate = "Échec et mat !"
  override val gamePromoteTitle = "Promouvoir à:"
  override val gamePromoteConfirm = "Ok".uppercase()

  override val gameSnackBarIllegalAction: String = "Action illégale, veuillez réessayer"
  override val gameSnackBarInternalFailure = "Défaut interne"
  override val gameSnackBarUnknownCommand: String = "Commande inconnue, veuillez réessayer"

  override val profileMatchTitle = { opponent: String -> "Contre $opponent" }
  override val profileWonByCheckmate = { moves: Int -> "Gagné par échec et mat après $moves coups" }
  override val profileWonByForfeit = { moves: Int -> "Gagné par forfait après $moves coups" }
  override val profileLostByCheckmate = { moves: Int ->
    "Perdu par échec et mat après $moves coups"
  }
  override val profileLostByForfeit = { moves: Int -> "Perdu par forfait après $moves coups" }
  override val profileTieInfo = { moves: Int -> "Egalité après $moves coups" }
  override val profileAgainst = "Contre " // TODO: handle right-to-left languages
  override val profileYourTurn = "A vous de jouer !"
  override val profileOthersTurn = "C'est son tour de jouer."

  override val settingEditSave = "Sauvegarder".uppercase()
  override val settingEditCancel = "Annuler".uppercase()
  override val settingProfileNameLabel = "Nom du profil"
  override val settingProfileImageLabel = "Image de profil"
  override val settingLogout: String = "Déconnexion"
  override val settingLanguageLabel = "Langue"
  override val settingsEditLanguage = "Changer langue"

  override val profilePastGames = "Jeux passés".uppercase()
  override val profilePuzzle = "Puzzles".uppercase()
  override val profileFollow = "Follow"
  override val profileUnfollow = "Unfollow"
  override val profileChallenge = "Challenge"
  override val profileEditImageIcon = "Icône de modification de l'image du profil"
  override val profileEditNameIcon = "Icône de modification du nom du profil"

  override val socialFollowingTitle = "Following"
  override val socialPerformPlay = "Jouer".uppercase()
  override val socialPerformFollow = "Follow".uppercase()
  override val socialPerformUnfollow = "Followed".uppercase()
  override val socialSearchBarPlaceHolder = "Rechercher un joueur ..."
  override val socialSearchEmptyTitle = "Recherchez n'importe quel joueur"
  override val socialSearchEmptySubtitle =
      "Trouves un joueur en utilisant son nom, suives-le, invites-le à jouer ou consultes l'historique de ses matchs."
  override val socialSearchClearContentDescription = "Effacer le champ de recherche"
  override val socialCloseVisitedProfile = "Fermer"

  override val sectionAr: String = "AR"
  override val sectionSocial = "Joueurs"
  override val sectionSettings = "Paramètres"
  override val sectionPlay = "Jouer"
  override val sectionPuzzles = "Puzzles"
  override val sectionContests = "Concours"

  override val newGame = "Nouveau jeu".uppercase()
  override val newContest = "Nouveau concours".uppercase()

  override val prepareGameChooseColor = "Choisissez votre couleur :"
  override val prepareGameChooseOpponent = "Choisissez un adversaire :"
  override val prepareGameChooseGame = "Choisissez un adversaire :"
  override val prepareGameWhiteColor = "Blanc".uppercase()
  override val prepareGameBlackColor = "Noir".uppercase()
  override val prepareGamePlay = "Jouer !".uppercase()
  override val prepareGameCancel = "Annuler".uppercase()
  override val prepareGameSelectOpponent = "Sélectionner".uppercase()
  override val prepareGameSelectedOpponent = "Sélectionné".uppercase()
  override val prepareGamePlayLocal = "Local".uppercase()
  override val prepareGamePlayOnline = "En ligne".uppercase()

  override val playOnlineGames = "Jeux en ligne"

  override val puzzlePlayingAs = { color: String -> "Jouer comme $color" }
  override val puzzleUnsolvedPuzzles = "Puzzles non résolus"
  override val puzzleListContentDescription = "Liste de puzzles"
  override val puzzlesTitle = "Puzzles"
  override val puzzleSolving = { color: String -> "Trouves le meilleur déménagement pour $color" }
  override val puzzleFailed = "Tu as échoué ! Essayes encore !"
  override val puzzleSolved = "Tu as gagné ! Félicitations !"
  override val puzzleNumber = { id: String -> "Puzzle: #$id" }
  override val puzzleRating = { rating: String -> "Rating: $rating" }

  override val tournamentsBadgeJoin = "Rejoignes"
  override val tournamentsBadgeParticipant = "Participant"
  override val tournamentsBadgeAdmin = "Admin"
  override val tournamentsTableScore = "Score"
  override val tournamentsPoolStillOpen = "Encore ouvert"
  override val tournamentsPoolRound = { current: Int, total: Int -> "Round $current / $total" }
  override val tournamentsPoolStartNextRound = "Commencer le prochain tour".uppercase()
  override val tournamentsDetailsFinals = "Finales".uppercase()
  override val tournamentsDetailsPools = "Pools".uppercase()
  override val tournamentsDetailsWatch = "Regarder".uppercase()
  override val tournamentsDetailsMatchWon = "Win".uppercase()
  override val tournamentsDetailsMatchLost = "Perte".uppercase()
  override val tournamentsDetailsMatchDrawn = "Tirer".uppercase()
  override val tournamentsDetailsHeaderOngoing = "En cours".uppercase()
  override val tournamentsDetailsHeaderDone = "Terminé".uppercase()
  override val tournamentsDetailsNextBestOfTitle = { round: Int, total: Int ->
    "Créer un match $round / $total"
  }
  override val tournamentsDetailsNextBestOfSubtitle =
      "Ajoutes un nouveau match à tous les joueurs de ce tour."
  override val tournamentsDetailsNextRoundTitle = "Prochain tour"
  override val tournamentsDetailsNextRoundSubtitle =
      "Faire passer tous les gagnants au tour suivant"
  override val tournamentsDetailsStartEnoughPlayersTitle = "Commencez le tournoi"
  override val tournamentsDetailsStartEnoughPlayersSubtitle =
      "Une fois lancé, le tournoi assignera les joueurs et les premiers matchs seront créés."
  override val tournamentsDetailsStartNotEnoughPlayersTitle = "Commencez le tournoi"
  override val tournamentsDetailsStartNotEnoughPlayersSubtitle =
      "Si tu commences le tournoi maintenant, il n'y aura pas assez de joueurs pour jouer tous les matchs."
  override val tournamentsDetailsStartDirectEliminationTitle = "Commencer l'élimination directe"
  override val tournamentsDetailsStartDirectEliminationSubtitle =
      "Calculer les résultats de la poule et commencer les matchs d'élimination directe."
  override val tournamentDetailsBackContentDescription = "Retour"
  override val tournamentDetailsPoolName = { poolNr: Int -> "Pool #${poolNr}" }

  override val tournamentsContestsTitle = "Concours"
  override val tournamentsFilter = "Filtre"
  override val tournamentsStartingTime = { duration: Duration, style: SpanStyle ->
    buildAnnotatedString {
      append("Commencé il y a ")
      withStyle(style) { append(duration.absoluteValue.toFrenchString()) }
    }
  }
  override val tournamentsDone = "Terminé"
  override val tournamentsCreateTitle = "Créer un tournoi"
  override val tournamentsCreateNameHint = "Nom"
  override val tournamentsCreateRules = "Règles"
  override val tournamentsCreateBestOf = "Best of :"
  override val tournamentsCreatePlayers = "Joueurs"
  override val tournamentsCreateMaximumPlayerHint = "Nombre maximum de joueurs"
  override val tournamentsCreatePoolSize = "Taille du pool :"
  override val tournamentsCreateDirectElimination = "Direct elim. :"
  override val tournamentsCreateActionCancel = "Annuler".uppercase()
  override val tournamentsCreateActionCreate = "Créer".uppercase()

  override val tournamentsCreateQualifierSize0 = "Aucun qualificatif"
  override val tournamentsCreateQualifierSizeN = { size: Int -> size.toString() }
  override val tournamentsCreateElimDemomN = { denominator: Int -> "1 / $denominator" }
  override val tournamentsCreateElimDepthFinal = "Final"

  override val tournamentsFilterTitle = "Afficher seulement".uppercase()
  override val tournamentsFilterOnlyDone = "Pas fait".uppercase()
  override val tournamentsFilterOnlyParticipating = "Participant".uppercase()
  override val tournamentsFilterOnlyAdministrating = "Administrer".uppercase()
  override val tournamentsFilterBackContentDescription = "Retour"
}

/**
 * Converts a [Duration] to an French string by rounding it to the closest unit of time (seconds
 * minimum).
 */
private fun Duration.toFrenchString(): String {
  if (this >= 1.days) {
    return "${this.inWholeDays} jour(s)"
  } else if (this >= 1.hours) {
    return "${this.inWholeHours} heure(s)"
  } else if (this >= 1.minutes) {
    return "${this.inWholeMinutes} minute(s)"
  }

  return "${this.inWholeSeconds} seconde(s)"
}
