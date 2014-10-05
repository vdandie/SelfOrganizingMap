/////////////////////////
//  SelfOrganizingMap  //
/////////////////////////
Written by Daniel Swain

1.0 	- 09/04/14

	+ The project works, but its untidy. Fix iminent

1.0.1 	- 09/09/14

	+ Added some functionalities.

1.1 	- 09/16/14

	+ Complete overhaul of the structure of the code.
	+ Much easier to read and stuff.

1.1.1 	- 09/23/14

	+ Added more functionalities. 

1.1.2 	- 09/30/14

	+ Fixed: RandRecordSet
		- getLeftovers() 	- was returning completely wrong outputs. FIXED
		- createTrainingSet	- because getLeftovers() wasn't working, this wasn't either. FIXED

	+ Made SOMEnhanced better:
		- firstTimeInitialization() is now run()
		- aquireTrainingSet() is now train(int numOfSets)
		- now accurately prints 10 trainingSets with readable records

	+ Known Bugs:
		- Need to make the output and trainingSet file numbers the same in every situation so it's easier to find the pairs
		- When making more than one training set, the records start to come out funny. Training sets 1 and 2 look decent.

1.1.3 	- 10/02/14

	+ Fixed the mysterious winner rule
		- cleanMatrix()		- Added a feature so that it will now clean the winners that don't have corresponding
								Records in the Matrix

	+ Changed the name of:
		- SelfOrganizingMap =>	AbstractSetCreator
		- SOM  				=>	SetCreator
		- SOMEnhanced		=>	SetCreatorEnhanced
		- Should be easier to understand now.

	+ TODO:
		- Build new package containing classes to handle the shooting of the test sets to the chosen training set
		
1.1.4	- 10/05/14

	+ Trying to fix: Multiple TrainingSet Problem
		- Currently when making multiple training sets, the records change throughout the queues, i.e.
			the records altered in the previous run are the same records used over again.

1.1.5	- 10/05/14

	+ Kind of fixed: Multiple TrainingSet Problem
		- Creating multiple trained sets used to be a problem, and is now fixed by the addition of the resetAlpha() function
		- Alpha wasn't being reset, therefore the updated values became smaller and smaller

	+ New Problem: Duplicates... Again.
		- Duplicates are showing up again even though being ran through cleanMatrix()