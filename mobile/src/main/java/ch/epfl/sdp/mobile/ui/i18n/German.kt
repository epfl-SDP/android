package ch.epfl.sdp.mobile.ui.i18n

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/** Localized strings for the English language. */
object German : LocalizedStrings {
  override val authenticateTitle = "Pawnies"
  override val authenticateSubtitleRegister =
      "Werden Sie Mitglied der Pawnies-Gemeinschaft.".uppercase()
  override val authenticateSubtitleLogIn = "Willkommen zurück bei Pawnies.".uppercase()
  override val authenticateEmailHint = "Dein Email"
  override val authenticateNameHint = "Ihr vollständiger Name"
  override val authenticatePasswordHint = "Passwort"
  override val authenticatePasswordToggleVisibility = "Toggle Passwort Sichtbarkeit"
  override val authenticatePerformLogIn = "Mit E-Mail anmelden".uppercase()
  override val authenticatePerformRegister = "Registrieren mit E-Mail".uppercase()
  override val authenticateSwitchToLogIn = "Anmelden".uppercase()
  override val authenticateSwitchToRegister = "Registrieren".uppercase()
  override val authenticateErrorFailure = "Bei der Authentifizierung ist etwas schief gelaufen"
  override val authenticateBadPasswordFailure =
      "Ihr Passwort sollte mindestens 6 Zeichen lang sein."
  override val authenticateWrongEmailFormatFailure =
      "Ihre E-Mail muss die Form: person@domain haben"
  override val authenticateInvalidUserFailure = "Es ist kein Benutzer mit dieser E-Mail registriert"
  override val authenticateIncorrectPasswordFailure =
      "Die eingegebene E-Mail oder das Passwort sind falsch"
  override val authenticateExistingAccountFailure =
      "Ein Benutzer mit dieser E-Mail existiert bereits"
  override val authenticateOr = "Oder"

  override val arContentDescription: String = "ArComposable"

  override val boardColorBlack = "schwarz"
  override val boardColorWhite = "weiss"
  override val boardPieceKing = "König"
  override val boardPieceQueen = "Dame"
  override val boardPieceRook = "Turm"
  override val boardPieceBishop = "Läufer"
  override val boardPieceKnight = "Springer"
  override val boardPiecePawn = "Bauer"
  override val boardPieceContentDescription = { color: String, rank: String -> "$color $rank" }
  override val boardContentDescription = "Schachbrett"

  override val gameBack = "Zurück"
  override val gameShowAr = "Starte AR Modus"
  override val gameMicOffContentDescription = "Mikrofon aus"
  override val gameMicOnContentDescription = "Mikrofon an"
  override val gameListening = "Anhören"
  override val gameMessageYourTurn = "Du bist dran"
  override val gameMessageCheck = "Schach !"
  override val gameMessageStalemate = "Pattsituation !"
  override val gameMessageCheckmate = "Schachmatt !"
  override val gamePromoteTitle = "Befördern zu:"
  override val gamePromoteConfirm = "Ok".uppercase()

  override val gameSnackBarIllegalAction: String = "Illegale Aktion, bitte versuchen Sie es erneut"
  override val gameSnackBarInternalFailure = "Internes Fehlverhalten"
  override val gameSnackBarUnknownCommand: String =
      "Unbekannter Befehl, bitte versuchen Sie es erneut"

  override val profileMatchTitle = { opponent: String -> "Gegen $opponent" }
  override val profileWonByCheckmate = { moves: Int ->
    "Gewonnen durch Schachmatt nach $moves Zügen"
  }
  override val profileWonByForfeit = { moves: Int -> "Gewonnen durch Aufgabe nach $moves Zügen" }
  override val profileLostByCheckmate = { moves: Int ->
    "Verloren durch Schachmatt nach $moves Zügen"
  }
  override val profileLostByForfeit = { moves: Int -> "Verloren durch Aufgabe nach $moves Zügen" }
  override val profileTieInfo = { moves: Int -> "Unentschieden nach $moves Zügen" }
  override val profileAgainst = "Gegen " // TODO: handle right-to-left languages
  override val profileYourTurn = "Du bist am Zug!"
  override val profileOthersTurn = "Der Andere ist am Zug."

  override val settingEditSave = "Speichern".uppercase()
  override val settingEditCancel = "Abbrechen".uppercase()
  override val settingProfileNameLabel = "Profil Name"
  override val settingProfileImageLabel = "Profile Bild"
  override val settingLogout: String = "Abmelden"
  override val settingLanguageLabel = "Sprachen"
  override val settingsEditLanguage = "Sprache ändern"

  override val profilePastGames = "Vergangene Spiele".uppercase()
  override val profilePuzzle = "Puzzles".uppercase()
  override val profileFollow = "Folgen"
  override val profileUnfollow = "Entfolgen"
  override val profileChallenge = "Herausfordern"
  override val profileEditImageIcon = "Profilbild bearbeiten icon"
  override val profileEditNameIcon = "Profilname bearbeiten icon"

  override val socialFollowingTitle = "Following"
  override val socialPerformPlay = "Spielen".uppercase()
  override val socialPerformFollow = "Folgen".uppercase()
  override val socialPerformUnfollow = "Followed".uppercase()
  override val socialSearchBarPlaceHolder = "Suche Spieler ..."
  override val socialSearchEmptyTitle = "Suche nach einem beliebigen Spieler"
  override val socialSearchEmptySubtitle =
      "Finde jeden Spieler anhand seines Namens, folgen ihm, laden ihn zum Spielen ein oder sieh dir sein Spielverlauf an."
  override val socialSearchClearContentDescription = "Suchfeld löschen"
  override val socialCloseVisitedProfile = "Schliessen"

  override val sectionAr: String = "AR"
  override val sectionSocial = "Spieler"
  override val sectionSettings = "Einstellungen"
  override val sectionPlay = "Spielen"
  override val sectionPuzzles = "Puzzles"
  override val sectionContests = "Turnier"

  override val newGame = "Neues Spiel".uppercase()
  override val newContest = "Neues Turnier".uppercase()

  override val prepareGameChooseColor = "Wähle deine Farbe :"
  override val prepareGameChooseOpponent = "Wähle dein Gegner:"
  override val prepareGameChooseGame = "Wähle dein Spiel :"
  override val prepareGameWhiteColor = "Weiss".uppercase()
  override val prepareGameBlackColor = "Schwarz".uppercase()
  override val prepareGamePlay = "Spielen !".uppercase()
  override val prepareGameCancel = "Abbrechen".uppercase()
  override val prepareGameSelectOpponent = "Wählen".uppercase()
  override val prepareGameSelectedOpponent = "Gewählt".uppercase()
  override val prepareGamePlayLocal = "Lokal".uppercase()
  override val prepareGamePlayOnline = "Online".uppercase()

  override val playOnlineGames = "Online Spiele"

  override val puzzlePlayingAs = { color: String -> "Spiele als $color" }
  override val puzzleUnsolvedPuzzles = "Ungelöste puzzles"
  override val puzzleListContentDescription = "Puzzleliste"
  override val puzzlesTitle = "Puzzles"
  override val puzzleSolving = { color: String -> "Finde der beste Zug für $color" }
  override val puzzleFailed = "Stimmt leider nicht! Versuche es noch einmal!"
  override val puzzleSolved = "Du hast gewonnen! Glückwunsch!"
  override val puzzleNumber = { id: String -> "Puzzle: #$id" }
  override val puzzleRating = { rating: String -> "Bewertung: $rating" }

  override val tournamentsBadgeJoin = "Beitreten"
  override val tournamentsBadgeParticipant = "Teilnehmer"
  override val tournamentsBadgeAdmin = "Admin"
  override val tournamentsTableScore = "Spielstand"
  override val tournamentsPoolStillOpen = "Noch offen"
  override val tournamentsPoolRound = { current: Int, total: Int -> "Runde $current / $total" }
  override val tournamentsPoolStartNextRound = "Beginn der nächsten Runde".uppercase()
  override val tournamentsDetailsFinals = "Finale".uppercase()
  override val tournamentsDetailsPools = "Pools".uppercase()
  override val tournamentsDetailsWatch = "Zuschauen".uppercase()
  override val tournamentsDetailsMatchWon = "Sieg".uppercase()
  override val tournamentsDetailsMatchLost = "Niederlage".uppercase()
  override val tournamentsDetailsMatchDrawn = "Gleichstand".uppercase()
  override val tournamentsDetailsHeaderOngoing = "Laufend".uppercase()
  override val tournamentsDetailsHeaderDone = "Fertig".uppercase()
  override val tournamentsDetailsNextBestOfTitle = { round: Int, total: Int ->
    "Erstelle Match $round / $total"
  }
  override val tournamentsDetailsNextBestOfSubtitle =
      "Füge allen Spielern in dieser Runde ein neues Spiel zu."
  override val tournamentsDetailsNextRoundTitle = "Nächste Runde"
  override val tournamentsDetailsNextRoundSubtitle = "Alle Gewinner kommen in die nächste Runde"
  override val tournamentsDetailsStartEnoughPlayersTitle = "Starte das Turnier"
  override val tournamentsDetailsStartEnoughPlayersSubtitle =
      "Sobald das Turnier gestartet ist, werden die Spieler zugewiesen und die ersten Spiele werden erstellt."
  override val tournamentsDetailsStartNotEnoughPlayersTitle = "Starte das Turnier"
  override val tournamentsDetailsStartNotEnoughPlayersSubtitle =
      "Wenn du jetzt mit dem Turnier beginnst, gibt es nicht genug Spieler, um alle Spiele zu spielen."
  override val tournamentsDetailsStartDirectEliminationTitle = "Start der direkten Eliminierung"
  override val tournamentsDetailsStartDirectEliminationSubtitle =
      "Berechnen Sie die Poolergebnisse und beginnen Sie mit den Direktausscheidungsspielen."
  override val tournamentDetailsBackContentDescription = "Zurück"
  override val tournamentDetailsPoolName = { poolNr: Int -> "Pool #${poolNr}" }

  override val tournamentsContestsTitle = "Wetkampf"
  override val tournamentsFilter = "Filter"
  override val tournamentsStartingTime = { duration: Duration, style: SpanStyle ->
    buildAnnotatedString {
      append("Ist vor")
      withStyle(style) { append(duration.absoluteValue.toGerman()) }
      append(" gestartet")
    }
  }
  override val tournamentsDone = "Fertig"
  override val tournamentsCreateTitle = "Erstelle Turnier"
  override val tournamentsCreateNameHint = "Name"
  override val tournamentsCreateRules = "Regeln"
  override val tournamentsCreateBestOf = "Best of :"
  override val tournamentsCreatePlayers = "Spieler"
  override val tournamentsCreateMaximumPlayerHint = "Maximale Spielerzahl"
  override val tournamentsCreatePoolSize = "Pool grösse :"
  override val tournamentsCreateDirectElimination = "Direkte elim. :"
  override val tournamentsCreateActionCancel = "Abbrechen".uppercase()
  override val tournamentsCreateActionCreate = "Erstellen".uppercase()

  override val tournamentsCreateQualifierSize0 = "Keiner ist qualifiziert"
  override val tournamentsCreateQualifierSizeN = { size: Int -> size.toString() }
  override val tournamentsCreateElimDemomN = { denominator: Int -> "1 / $denominator" }
  override val tournamentsCreateElimDepthFinal = "Final"

  override val tournamentsFilterTitle = "Zeig nur".uppercase()
  override val tournamentsFilterOnlyDone = "Nicht fertig".uppercase()
  override val tournamentsFilterOnlyParticipating = "Teilnehmen".uppercase()
  override val tournamentsFilterOnlyAdministrating = "Verwatet".uppercase()
  override val tournamentsFilterBackContentDescription = "Zurück"
}

/**
 * Converts a [Duration] to an German string by rounding it to the closest unit of time (seconds
 * minimum).
 */
private fun Duration.toGerman(): String {
  if (this >= 1.days) {
    return "${this.inWholeDays} Tage"
  } else if (this >= 1.hours) {
    return "${this.inWholeHours} Stunden"
  } else if (this >= 1.minutes) {
    return "${this.inWholeMinutes} Minuten"
  }

  return "${this.inWholeSeconds} Sekunden"
}
