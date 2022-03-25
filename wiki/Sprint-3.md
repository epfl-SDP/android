# Sprint 2

## Summary

We manage to implement almost all the task. 
Most of the PR ask a review at the end of the week, which is a bit stressful. We can solve this by creating a smaller task and create multiple PR for one features.
We see that our organization is perfect (The naming convention for tests for example), but we solve these problems the quickest possible.
Some bug/missing implementation are solved in a PR without creating an Issue. 
But in general, it was a good sprint. We are able to present a good demo with new features.

## Lars Barmettler

This week I really made progress in my performance. I understood the basic principles of the technologies and can now rapidly apply simple changes. This makes the work a lot more fun because I can concentrate myself more on the interesting technical problems. On the workflow I think we are arrived on a good level, which we can hold until the last week of the project. The only thing I would wish would be improved is a faster review process. When I finished my task on Tuesday evening, I needed to wait 24h to get the first review. After this I did some changes on my PR I needed to wait another day. And finally, I’m not even sure if the PR getting merged until the Meeting with the TA’s. However, I assume later when I write more proper code this process getting less and less iteration and I can faster merge my code into the main.

## Matthieu Burguburu

My tasks for this sprint were implementing support for online games between two players using Firestore and the chess engine. Overall, things went quite smoothly for the implementation of the feature but were a bit more difficult for the testing phase, for technical reasons involving coroutines. My time estimates would have been pretty accurate without this problem

## Chau Ying Kot (Scrum Master)

For this sprint, beside the scrum master role, I was in charge of only one task, which was to show something in AR.
It was more challenging than what I imagined. ARCore detects the environment, to be able to display something, we need a renderer. Google has provided an open sourced renderer name SceneForm, but they archived this project 2 years ago. But we are lucky, because someone has continued the project (SceneView) and implement the missing features gradually.
This continuation is still recent and not perfect; they don’t integrate Compose yet. But with Alexandre's help, we manage to use SceneView with Compose.
It’s not easy to understand SceneView, because I lack of knowledge in this field and the documentation are not complete.
In addition, I take some time to help Fouad to fix the report path in Jacoco
I take time to do my task, but now I have a good understanding of the library and I have setup my work environment to be able to develop with AR, hence I can help other teammates that will use AR in the future.

## Fouad Mahmoud

This sprint was a lot better for me than the previous ones. Even though I had to refactor my old pull request which took me some time, my new tasks were in the same domain as those of the last sprint which easened my work and reduced my sprint hours considerably based on last week. I did have some assistance from Chau to help verify my Jacoco fix and Alexandre to help debug some tests and fasten up my work process which helped increase my kotlin and compose knowledge. 
 
Next sprint I will try to be more on track with my work, ask questions early and explore new code areas to enhance my knowledge even further

## Alexandre Piveteau 

My main task for this spring was to implement rules into the chess engine. This included making sure that all the pieces are allowed to move according to their standard rules, but also implementing special moves such as castling or the en-passant take. I also added check, checkmate and stalemate detection to the chess engine.

Additionally, I helped some of my teammates to implement online sync of games, user following or integrating AR with Compose.

I didn’t face major unforeseen issues and my time estimates were appropriate.

## Mohamed Badr Taddist 

This sprint went well in general, my task was to implement the preparation game screen from pre made mockups. Those were not very clear and it took some time to quite understand their limitations. I managed, however, to implement a first version of it. And the team concluded that we will still have to come up with a better design for the next sprint. My time estimates were  bot off this time though, mainly due some technicalities.