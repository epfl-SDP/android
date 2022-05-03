# Sprint 5 (first half)

## Lars Barmettler
In the first week of the sprint, I had only limited time to work for the SDP project, since we had two Midterms. However, I’ve managed to design the profile edit screen and implement the edit username feature. Yet I was not able to get the PR merged, since I faced some technical problems that I want to solve in person with someone else. The teamwork and the communication worked well for me and I didn’t had any other issues.

## Matthieu Burguburu (Scrum master)
This sprint, I was tasked with being the Scrum master as well as working on the new puzzles features.

My duties as Scrum master were not the easiest, as many midterms are taking place for everybody throughout the sprint. I did my best to try not to over-burden the team, while trying to make the project progress as much as possible. While some had no trouble accomplishing their tasks, some others had more trouble doing so. I reached out to them to help them get back on track to be able to complete their tasks by the end of the sprint. There was also some teamwork argument that took place over the vacation, but the incident was quickly and peacefully resolved before I had to do anything.

Concerning my tasks on the puzzles features, I spent the first week of the sprint as well as some of the vacation designing the puzzle screen on Figma, doing some research about existing chess puzzle application in order to get a clean and easily accessible source of puzzles to use for ourselves. I have now started implementing a first prototype of the puzzles screen which should be done by the end of the sprint.

## Chau Ying Kot
The first week of the sprint was a bit overwhelming, 2 exams were scheduled for Monday and Thursday. I had chosen to prioritize my revisions, thus I only started to work on SPD on Wednesday and finished my task during the break.
My assigned work for the first week of the sprint is to refactor the current ChessBoardState to facilitate the integration of the AR view by creating an interface that describe functions that are common for the 2D and 3D game. This 1st part is merged on the main branch. The 2 part is to use the new state on the AR view that allow the AR view to observe the modification on the board. This part is a bit complex, because create a new state specific for the AR view require a big change in ours code. We decide that we can reuse what are used for the 2D game despite that we have some functions that are unused by the AR game, that allow us to move forward.
The 2nd week of the sprite is dedicated to move pieces according to the state of the game.

## Fouad Mahmoud
For this sprint, I was tasked with creating enhancements to the authentication screen’s error messages display and make user matches in the profile and settings screens display the corresponding live or finished matches once clicked upon.

For this first sprint week, I finalised my authentication error messages branch which was merged last Wednesday. I also updated my profile game history branch with enhancements and code refactoring and made users’ profile and settings matches display the corresponding matches in the play screen.

What I still need to do is add tests to my match display implementation and check if any other refactoring in my profile game history branch needs to take place.

## Alexandre Piveteau
This sprint, my two main tasks consist in increasing the chess engine performance, and implementing the promotion rules with its associated user interface.

During the first week of the sprint, I found and removed multiple performance bottlenecks in the game engine, in particular by allowing the chessboard state to be computed incrementally. I fixed some UI-related issues, and implemented support for the promotion rules and its associated UI. However, the latter hasn’t been merged in the codebase yet; I would still like to increase the code coverage of the patch before it gets integrated. I’m therefore well on track to finish these tasks by the end of the sprint.

I also did some exploratory work on re-organizing our test packages, to be able to share some test infrastructure code across the unit and integration tests source sets. I looked into integrating Robolectric in our testing suite to speed it up; however, Robolectric seems to have some open issues with Jetpack Compose, so we probably won’t be using it right away.

## Mohamed Badr Taddist
The sprint is going great, however, I am still stuck for the test to merge the demo PR. Filtering the speech recognizer output using naïve filtering and adjustments is very error prone and sometimes does not give good results or nothing at all. After thinking a while and doing some research I considered using google cloud service API for speech recognition, which is more flexible and support language enhancements instead of android.speech module. The only downside is that it requires internet access.