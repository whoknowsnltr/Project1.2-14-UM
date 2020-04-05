READ ME
To run the code - run the DesktopLauncher.java file, that is in desktop/src/com/mydgx/game/desktop directory
The files that make up the game are in core/src/com/mydgx/game directory

Describtion of classes:
GAME ->
MyGame – main file, allows to run game
MyActor – creates actors, so that if we press on them, they follow mouse movements
MENU->
MenuScreen- class that contains all the screens and allows to change between them (the main screen, we can access the game from that, or the course creator)
COURSECREATOR ->
CourseCreator – class that allows to create default course also contains a window that allows user to input equations for the terrain, and graviy acceleration, mass of ball, friction etc
SaveCourse – class that saves a given course in a separate text file.
COURSETOPLAY->
Course – class that loads in the course picked by the player, has ball controller, method that throws the ball -> All the main game actions are in this class
PHYSICS ->
Classes are as given by the project manual, with one exeption:
FunctionReader -> This class evaluates the function of the terrain, and computes height for a given vector, or derivatives 

