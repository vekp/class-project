# Bitwise Operators

- Matthew Hawkins - <mhawki25@myune.edu.au>
- Todd McDouall - <tmcdoua2@myune.edu.au>
- Daniel Lake - <dlake2@myune.edu.au>
- Tony Huynh - <thuynh23@myune.edu.au> & <huynh.tony55@gmail.com>


## Description

Our group has developed a Survey feature that has the ability to be added to other teams minigames or features. 
Upon completion of an minigame that has integrated our Survey, users will be able to access the survey and conduct a short questionnaire to provide feedback on the game they have just completed.
Once the survey has been completed and feedback provided, users will be sent to the ratings page, which displays the total average ratings of all surveys conducted for a particular minigame.
Users can then use a dropdown menu to select other minigames or features and also see the ratings of those minigames aswell.

## Demo video

Please embed or link your demo video here.

- [TODO]()

## Difficulties we overcame

1. Getting a background image to display on our Jpanel was a significant time effort to firstly understand how Java Swing implements UI display as well as creating a working background, the implementation of this only ended up being a small amount of code.
2. Figuring out how to display multiple pages on the Frame by replacing Jpanels with different Jpanels took some time. This also required a solution that was portable and able to be called within other teams code files without interruption. 
3. General re-familiarisation with a more explicitly typed language in Java and Java Swing. Most members of our group are more familiar with other languages and took for granted features and functionality that is normally provided without explicit declarations.
4. In the original implementation of the feature to save survey data to JSON ([here](https://gitlab.une.edu.au/cosc220-2023/classproject/-/commit/e7acd7e5ab3a11b41cd95274e524a670f90cf4c0) and [here](https://gitlab.une.edu.au/cosc220-2023/classproject/-/commit/c289a767de719101cc9e031c3b9cdda98ab4d0fe)), we had not thought about the technicalities of appending new data to existing data. Thus, the code in its early stages was overwriting data every time the server and client were restarted. The solution [here](https://gitlab.une.edu.au/cosc220-2023/classproject/-/commit/dcfb48126eda2bc2968d319335101c10466a71df) was to remove the hardcoded file saving and use JSONParser to read existing data. Finally, that logic was refactored to save the survey data to the database, as well as read from it [here](https://gitlab.une.edu.au/cosc220-2023/classproject/-/commit/a3aa13ccc27995ce304fcb450ba659c9eee1c582).
5. Integrating our survey into a communal database was an issue as it was getting late in the project with no communal database yet available. We had to come up with our own solution in the interim, by utilising MongoDB we were able to overcome this issue.
6. Integrating enpoints into frontend and working with Futures. We had no problems sending data, however when it came to getting data and pulling it in to the frontend, it proved difficult. Using the ‘invokeLater()’ function we could then just re-set the text in each field.

## WIKI Pages & Features:

### Group page:  

- [Bitwise Operators Wiki Page](https://gitlab.une.edu.au/cosc220-2023/classproject/-/wikis/Bitwise%20Operators)

### Feature pages:

- [Survey Wiki Page](https://gitlab.une.edu.au/cosc220-2023/classproject/-/wikis/MiniGame-Survey)

## Group Branch Names

Our team decided to work on particular branches for specific features. We have generally been creating branches with the prefix of 9- . As we are the 9th team in the wiki.

This method was generally followed throughout, however some branches were named via gitlab issues.

All commits, however, have been merged into main.

- [9-Survey-Main](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-Survey-Main?ref_type=heads)
- [9-NewSurveyResults](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-NewSurveyResults?ref_type=heads)
- [9-ResultsPage](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-ResultsPage?ref_type=heads)
- [9-UISurveyFeedback](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-UISurveyFeedback?ref_type=heads)
- [9-SurveyUIStyling](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-SurveyUIStyling?ref_type=heads)
- [9-Survey-Data](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-Survey-Data?ref_type=heads)
- [67-bitwise-operators-improve-ui](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/67-bitwise-operators-improve-ui?ref_type=heads)
- [9-surveyUI](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-surveyUI?ref_type=heads)
- [9-Survey-Client-Testing](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-Survey-Client-Testing?ref_type=heads)
- [9-Survey-Database](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-Survey-Database?ref_type=heads)
- [9-Survey-Endpoints](https://gitlab.une.edu.au/cosc220-2023/classproject/-/tree/9-Survey-Endpoints?ref_type=heads)


- 9-Survey NO REMOTE
- 9-ResultsPage-2 NO REMOTE


## Issues 

- [Bitwise Operators - Survey Framework](https://gitlab.une.edu.au/cosc220-2023/classproject/-/issues/15)
- [Bitwise Operators - Create Survey Foundation Code](https://gitlab.une.edu.au/cosc220-2023/classproject/-/issues/29)
- [Bitwise Operators - Create Basic User Interface](https://gitlab.une.edu.au/cosc220-2023/classproject/-/issues/55)
- [Bitwise Operators - Integrate Survey with other Teams Minigames](https://gitlab.une.edu.au/cosc220-2023/classproject/-/issues/30)
- [Bitwise Operators - Improve UI](https://gitlab.une.edu.au/cosc220-2023/classproject/-/issues/67)
- [Bitwise Operators - Implement Survey Critique Feedback](https://gitlab.une.edu.au/cosc220-2023/classproject/-/issues/106)
- [Bitwise Operators – Survey data storage solution](https://gitlab.une.edu.au/cosc220-2023/classproject/-/issues/46)
- [Bitwise Operators - Complete MVP](https://gitlab.une.edu.au/cosc220-2023/classproject/-/issues/107)
- [Bitwise Operators - Final Integration of Survey into Games](https://gitlab.une.edu.au/cosc220-2023/classproject/-/issues/218)

## Main Classes

- [Survey Class](https://gitlab.une.edu.au/cosc220-2023/classproject/-/blob/main/javaprojects/client/src/main/java/minigames/client/survey/Survey.java)
- [SurveyResults Class](https://gitlab.une.edu.au/cosc220-2023/classproject/-/blob/main/javaprojects/client/src/main/java/minigames/client/survey/SurveyResults.java)
- [SurveyServerRequestService Class](https://gitlab.une.edu.au/cosc220-2023/classproject/-/blob/main/javaprojects/client/src/main/java/minigames/client/survey/SurveyServerRequestService.java)
- [MongoDB Class](https://gitlab.une.edu.au/cosc220-2023/classproject/-/blob/main/javaprojects/server/src/main/java/minigames/server/survey/MongoDB.java)
- [SurveyHelperFunctions Class](https://gitlab.une.edu.au/cosc220-2023/classproject/-/blob/main/javaprojects/server/src/main/java/minigames/server/survey/SurveyHelperFunctions.java)
- [SurveyRoutesHandler Class](https://gitlab.une.edu.au/cosc220-2023/classproject/-/blob/main/javaprojects/server/src/main/java/minigames/server/survey/SurveyRoutesHandler.java)


Include the GitLab link. This lets you choose whether to link to
a version on Main or on your group branch.


## A list of the tests you have created

Link to the code on GitLab

ADD TEST CODE LINK HERE

## Notes

All our commits were pushed individually based on Issues that were created.

