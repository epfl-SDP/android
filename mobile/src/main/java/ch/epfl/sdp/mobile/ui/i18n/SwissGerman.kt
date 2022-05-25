package ch.epfl.sdp.mobile.ui.i18n

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/** Localized strings for the English language. */
object SwissGerman : LocalizedStrings {
  override val authenticateTitle = "Pawnies"
  override val authenticateSubtitleRegister = "Wird Mitglied vo de Pawnies community.".uppercase()
  override val authenticateSubtitleLogIn = "Willkommä zrug bi Pawnies.".uppercase()
  override val authenticateEmailHint = "Dini Email"
  override val authenticateNameHint = "Din vollä Name"
  override val authenticatePasswordHint = "Passwort"
  override val authenticatePasswordToggleVisibility = "Toggle d'Passwort Sichtbarkeit"
  override val authenticatePerformLogIn = "Meld di a mit Email".uppercase()
  override val authenticatePerformRegister = "Registrier di mit Email".uppercase()
  override val authenticateSwitchToLogIn = "Meld di a".uppercase()
  override val authenticateSwitchToRegister = "Registrier di".uppercase()
  override val authenticateErrorFailure = "Hoppla, öbis isch schief gloffe während de Amäulidg"
  override val authenticateBadPasswordFailure = "Dis Passwort muen mindestens 6 haa"
  override val authenticateWrongEmailFormatFailure = "Dini Email mues d Form: person@domain haa"
  override val authenticateInvalidUserFailure = "Es git kein Benutzer mit derä Email"
  override val authenticateIncorrectPasswordFailure = "D Email oder s Passwort isch ned korräkt"
  override val authenticateExistingAccountFailure = "En Benutzer mit dere Email gits scho"
  override val authenticateOr = "odr"

  override val arContentDescription: String = "ArComposable"

  override val boardColorBlack = "Schwarz"
  override val boardColorWhite = "Wiis"
  override val boardPieceKing = "König"
  override val boardPieceQueen = "Damä"
  override val boardPieceRook = "Turm"
  override val boardPieceBishop = "Läufer"
  override val boardPieceKnight = "Springer"
  override val boardPiecePawn = "Puur"
  override val boardPieceContentDescription = { color: String, rank: String -> "$color $rank" }
  override val boardContentDescription = "Schachbrätt"

  override val gameBack = "Zrug"
  override val gameShowAr = "Start AR Modus"
  override val gameMicOffContentDescription = "Mikrofon uus"
  override val gameMicOnContentDescription = "Mikrofon aa"
  override val gameListening = "Loose"
  override val gameMessageYourTurn = "Du bisch dra"
  override val gameMessageCheck = "Schach !"
  override val gameMessageStalemate = "Gliichstand !"
  override val gameMessageCheckmate = "Schachmatt !"
  override val gamePromoteTitle = "Befärdere zu:"
  override val gamePromoteConfirm = "Ok".uppercase()

  override val gameSnackBarIllegalAction: String = "Kei gültigi Aktion, versuechs nomal"
  override val gameSnackBarInternalFailure = "Interne Fähler"
  override val gameSnackBarUnknownCommand: String = "Befehl isch unbekannt, versuechs no einisch"

  override val profileMatchTitle = { opponent: String -> "Gägä $opponent" }
  override val profileWonByCheckmate = { moves: Int -> "Gunne wege Schachmatt nach $moves Züg" }
  override val profileWonByForfeit = { moves: Int -> "Gunne wege Ufgab nach $moves Züg" }
  override val profileLostByCheckmate = { moves: Int -> "Verlore wege Schachmatt nach $moves Züg" }
  override val profileLostByForfeit = { moves: Int -> "Verlore wege Ufgab nach $moves Züg" }
  override val profileTieInfo = { moves: Int -> "Gliichstand nach $moves Züg" }
  override val profileAgainst = "Gäge " // TODO: handle right-to-left languages
  override val profileYourTurn = "Du bisch dra!"
  override val profileOthersTurn = "De andr isch dra."

  override val settingEditSave = "Speicherä".uppercase()
  override val settingEditCancel = "Abbräche".uppercase()
  override val settingProfileNameLabel = "Profiu Name"
  override val settingProfileImageLabel = "Profiu Biuld"
  override val settingLogout: String = "Uslogge"
  override val settingLanguageLabel = "Sproch"
  override val settingsEditLanguage = "Sproch änderä"

  override val profilePastGames = "Voherigi Spiu".uppercase()
  override val profilePuzzle = "Puzzles".uppercase()
  override val profileFollow = "Follow"
  override val profileUnfollow = "Unfollow"
  override val profileChallenge = "Herusforderä"
  override val profileEditImageIcon = "Profiu biuld bearbeitä icon"
  override val profileEditNameIcon = "Profiu name bearbeitä icon"

  override val socialFollowingTitle = "Following"
  override val socialPerformPlay = "Spielä".uppercase()
  override val socialPerformFollow = "Follow".uppercase()
  override val socialPerformUnfollow = "Followed".uppercase()
  override val socialSearchBarPlaceHolder = "Suechä Spieler ..."
  override val socialSearchEmptyTitle = "Suechä irgendein Spieler"
  override val socialSearchEmptySubtitle =
      "Find irgend en Spieler mit sim Name, folg ihn, odr lad ihn i um sini altä Spiu azluege"
  override val socialSearchClearContentDescription = "Suech fäld löschä"

  override val sectionAr: String = "AR"
  override val sectionSocial = "Spieler"
  override val sectionSettings = "Istellige"
  override val sectionPlay = "Spilä"
  override val sectionPuzzles = "Puzzles"
  override val sectionContests = "Turnier"

  override val newGame = "Nois Spiel".uppercase()
  override val newContest = "Nois Turnier".uppercase()

  override val prepareGameChooseColor = "Wähl dini Farb :"
  override val prepareGameChooseOpponent = "Wähl din Gägner :"
  override val prepareGameChooseGame = "Wähl din Name :"
  override val prepareGameWhiteColor = "Wiis".uppercase()
  override val prepareGameBlackColor = "Schwarz".uppercase()
  override val prepareGamePlay = "Spielä !".uppercase()
  override val prepareGameCancel = "Abbrächä".uppercase()
  override val prepareGameSelectOpponent = "Uswähle".uppercase()
  override val prepareGameSelectedOpponent = "Usgwählt".uppercase()
  override val prepareGamePlayLocal = "Lokal".uppercase()
  override val prepareGamePlayOnline = "Online".uppercase()

  override val playOnlineGames = "Online spiul"

  override val puzzlePlayingAs = { color: String -> "Spiu als $color" }
  override val puzzleUnsolvedPuzzles = "Unsolved puzzles"
  override val puzzleListContentDescription = "Puzzles list"
  override val puzzlesTitle = "Puzzles"
  override val puzzleSolving = { color: String -> "Gfind de besti Zug für $color" }
  override val puzzleFailed = "Het leider ned gstummä! Versuechs nu einisch!"
  override val puzzleSolved = "Du heschs gschaft! Congrats!"
  override val puzzleNumber = { id: String -> "Puzzle: #$id" }
  override val puzzleRating = { rating: String -> "Rating: $rating" }

  override val tournamentsBadgeJoin = "Biiträte"
  override val tournamentsBadgeParticipant = "Teilnähmer"
  override val tournamentsBadgeAdmin = "Admin"
  override val tournamentsTableScore = "Score"
  override val tournamentsPoolStillOpen = "Immerno offä"
  override val tournamentsPoolRound = { current: Int, total: Int -> "Rundi $current / $total" }
  override val tournamentsPoolStartNextRound = "Start d nächst Rundi".uppercase()
  override val tournamentsDetailsFinals = "Finalä".uppercase()
  override val tournamentsDetailsPools = "Pools".uppercase()
  override val tournamentsDetailsWatch = "Zueluegä".uppercase()
  override val tournamentsDetailsMatchWon = "Gunnä".uppercase()
  override val tournamentsDetailsMatchLost = "Verlohrä".uppercase()
  override val tournamentsDetailsMatchDrawn = "Glichstand".uppercase()
  override val tournamentsDetailsHeaderOngoing = "Lauft immerno".uppercase()
  override val tournamentsDetailsHeaderDone = "Fertig".uppercase()
  override val tournamentsDetailsNextBestOfTitle = { round: Int, total: Int ->
    "Erstell en neuä Match $round / $total"
  }
  override val tournamentsDetailsNextBestOfSubtitle =
      "Duen allne Spieler i dere Rundi es neus Spiu zueteilä."
  override val tournamentsDetailsNextRoundTitle = "Nächsti Rundi"
  override val tournamentsDetailsNextRoundSubtitle = "Due all Günner i d nächst Rundi neh"
  override val tournamentsDetailsStartEnoughPlayersTitle = "Start s Turnier"
  override val tournamentsDetailsStartEnoughPlayersSubtitle =
      "Nach em Turnierstart, werded d Spieler zuegwisse und die erstä Spiu werded erstellt."
  override val tournamentsDetailsStartNotEnoughPlayersTitle = "Startä s Turnier"
  override val tournamentsDetailsStartNotEnoughPlayersSubtitle =
      "Wenn etztä s Turnier startisch, denn hets ned gnüegend Spieler um alli Mätch zum spilä."
  override val tournamentsDetailsStartDirectEliminationTitle = "Start direkti uuscheidig"
  override val tournamentsDetailsStartDirectEliminationSubtitle =
      "Berächne die Pool grössene und startä die direkt uuscheidig Mätch."
  override val tournamentDetailsBackContentDescription = "Zrugg"
  override val tournamentDetailsPoolName = { poolNr: Int -> "Pool #${poolNr}" }

  override val tournamentsContestsTitle = "Turnier"
  override val tournamentsFilter = "Fiultr"
  override val tournamentsStartingTime = { duration: Duration, style: SpanStyle ->
    buildAnnotatedString {
      append("Het vor ")
      withStyle(style) { append(duration.absoluteValue.toSwissGermanString()) }
      append(" gstartet")
    }
  }
  override val tournamentsDone = "Fertig"
  override val tournamentsCreateTitle = "Erstell es Turnier"
  override val tournamentsCreateNameHint = "Name"
  override val tournamentsCreateRules = "Rägle"
  override val tournamentsCreateBestOf = "Best of :"
  override val tournamentsCreatePlayers = "Spieler"
  override val tournamentsCreateMaximumPlayerHint = "Maximum Spielerzahl"
  override val tournamentsCreatePoolSize = "Pool grössi :"
  override val tournamentsCreateDirectElimination = "Direkti elim. :"
  override val tournamentsCreateActionCancel = "Abbrächä".uppercase()
  override val tournamentsCreateActionCreate = "Erställe".uppercase()

  override val tournamentsCreateQualifierSize0 = "Kei Qualifizierti"
  override val tournamentsCreateQualifierSizeN = { size: Int -> size.toString() }
  override val tournamentsCreateElimDemomN = { denominator: Int -> "1 / $denominator" }
  override val tournamentsCreateElimDepthFinal = "Final"

  override val tournamentsFilterTitle = "Zeig nur".uppercase()
  override val tournamentsFilterOnlyDone = "Nöd fertig".uppercase()
  override val tournamentsFilterOnlyParticipating = "Teilnä".uppercase()
  override val tournamentsFilterOnlyAdministrating = "Verwautä".uppercase()
  override val tournamentsFilterBackContentDescription = "Zrugg"
}

/**
 * Converts a [Duration] to an English string by rounding it to the closest unit of time (seconds
 * minimum).
 */
private fun Duration.toSwissGermanString(): String {
  if (this >= 1.days) {
    return "${this.inWholeDays} Täg"
  } else if (this >= 1.hours) {
    return "${this.inWholeHours} Stundä"
  } else if (this >= 1.minutes) {
    return "${this.inWholeMinutes} Minutä"
  }

  return "${this.inWholeSeconds} Sekundä"
}
