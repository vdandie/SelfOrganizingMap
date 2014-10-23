#SelfOrganizingMapV2  
##Change Log

##Written by Daniel Swain Jr

###1.0 	- 09/04/14

	+ The project works, but its untidy. Fix iminent

###1.0.1 	- 09/09/14

	+ Added some functionalities.

###1.1 	- 09/16/14

	+ Complete overhaul of the structure of the code.
	+ Much easier to read and stuff.

###1.1.1 	- 09/23/14

	+ Added more functionalities. 

###1.1.2 	- 09/30/14

	+ Fixed: RandRecordSet
		- getLeftovers() 	
			was returning completely wrong outputs. FIXED
		- createTrainingSet	
			because getLeftovers() wasn't working,
			this wasn't either. FIXED

	+ Made SOMEnhanced better:
		- firstTimeInitialization() is now run()
		- aquireTrainingSet() is now train(int numOfSets)
		- now accurately prints 10 trainingSets with readable records

	+ Known Bugs:
		- Need to make the output and trainingSet file numbers the 
			same in every situation so it's easier to find the pairs
		- When making more than one training set, the records start 
			to come out funny. Training sets 1 and 2 look decent.

###1.1.3 	- 10/02/14

	+ Fixed the mysterious winner rule
		- cleanMatrix()		
			Added a feature so that it will now clean the winners
			 that don't have corresponding records in the matrix

	+ Changed the name of:
		- SelfOrganizingMap =>	AbstractSetCreator
		- SOM  				=>	SetCreator
		- SOMEnhanced		=>	SetCreatorEnhanced
		- Should be easier to understand now.

	+ TODO:
		- Build new package containing classes to handle the
			shooting of the test sets to the chosen training set
		
###1.1.4	- 10/05/14

	+ Trying to fix: Multiple TrainingSet Problem
		- Currently when making multiple training sets, the records
		 	change throughout the queues, i.e. the records altered in 
		 	the previous run are the same records used over again.

###1.1.5	- 10/05/14

	+ Kind of fixed: Multiple TrainingSet Problem
		- Creating multiple trained sets used to be a problem,
			 and is now fixed by the addition of the resetAlpha() function
		- Alpha wasn't being reset, therefore the updated values
			 became smaller and smaller

	+ New Problem: Duplicates... Again.
		- Duplicates are showing up again even though being ran 
			through cleanMatrix()

###1.2.0 	- 10/07/14

	+ Kind of fixed: Certainty Factor/Cover Problem
		- If a Rule(aka Seed) had it's dominant decision changed
			it wouldn't count itself as a winner and the cover
			and certainty factor would be off.

	+ Added functions to: Intersector
		- (void)read(int) 	
			// Reads the files of the corresponding int
		- (void)readTestSetFile(int)	
			// Reads the test set file of the corresponding int
		- (void)readTrainingSetFile(int)
			// Reads the training set file of the corresponding int
		- (void)run()		
			// Runs the algorithm

	+ Added functions to: Record
		-Record(Record,String)
			// Constructor that creates a record with a name

	+ Still A Problem: Duplicates... Again.
		- The duplicates seem to be somewhat different from each other
			therefore are not being caught by the cleanMatrix()
		- Will ask Dr Hashemi about this.

	+ TODO:
		- implement run() in Intersector
		- Do something about the semi-duplicates

###1.2.1 	- 10/08/14
	
	+ Created:
		- Intersector.runAlgorithm() - Sends each test record against
			all rules in the trainingSet
		- Intersector.sumAbsDif() - Returns the sum of the absolute 
			value differences ofattributes between two given records.
		- Intersector.times	- Number of files/times to do the algorthim
		- Intersector.match - A map where the key is the file number
			and the value is a double array of size 2 containing:
				1. The # of Matches that came through
				2. The Size of the TrainingSet

	+ Implemented run()
		-Uses runAlgorithm on each of the specified number of sets

	+ Changed the name of:
		- Record.isWinner() => Record.hasSameDecision()

	+ TODO:
		- Make into files
		- Other things...

###1.2.2 	- 10/08/14

	+ Created:
		-Intersector.results() - ArrayList<String> that contains the formatted
			results of each run.
		-Intersector.resultsToFile(String) - Creates files in a directory
			for the results

	+ Changes in:
		- Intersector.runAlgorithm() - Added functionalities for printing

	+ TODO:
		- The theres an offset because printing of the files starts at
			1, not a huge problem, but should go back and fix.

###1.2.3 	- 10/09/14

	+Created:
		-Intersector.totalResultsFile() - takes the totals of all tests and
			puts them in one file called "overall.txt"

	+ TODO:
		- Round off the records in the training sets to the thousandths 
			after training is complete and re-clean matrix

###1.2.4	- 10/13/14

	+ Created:
		- Matrix.round(double, int) - rounds the given value to the given number of places

	+Changes in:
		- Intersector.sumAbsDif() - changes so that the values added are either 0 or 1.
		- Intersector.runAlgorithm() - added functionality for true/false pos/neg table.
		- Matrix.updateRecord() - values are now rounded to the thousandths

	+ TODO:
		- Negative values... How are they made?!

###2.0.01	- 10/16/14
	
	Summary:
		The SetCreatorEnhanced.shootRecords() has been changed so that in the case of 
		multiple rules being the neighbor of a record and also sharing the same decision,
		the node with the largest cluster is chosen.

	+ Changed the name of:
		- SetCreatorEnhanced.findWinner() => doAlgorithm()
		- MatrixEnhanced.winners => clusters

	+ Created:
		- SetCreator.readOrigTrainingSetFile() - reads in the origTrainingSet files from
		the trainingSets folder and adds them to the trainingSet array.
		- SetCreatorEnhanced.runForPreCreatedSets() - does the same as run except that
		it's for pre-created training sets
		- SetCreatorEnhanced.trainPreCreatedSets() - trains all pre-created training sets
		- SetCreatorEnhanced.shootRecords_2() - does the same as shootRecords except that
		it's for pre-created training sets and does not need to use RandomSet.createQueue()
		and also does not print the origTrainingSets
		- MatrixEnhanced.getClusterSize() - returns the size of a rule's cluster

	+ Changes in:
		- SetCreatorEnahnced.doAlgorithm() - Case 3 has further constraints: if there are
		multiple winners, check their decisions. If there are still multiple decision 
		matching winners, compare cluster sizes. The rule with the largest cluster size
		is then updated by the record.

###2.0.02 	- 10/23/14
	
	+ Summary:
		Adding functionalities for k-1 case: 1 test record is created and the rest are used
		as a training set. The trainingSet is made up of 1 - the smaller decision groups value

	+ Created:
		-RandRecordSet.createSpecialTestSet() - Creates a queue array containing queues of
		each individual record.

	Changes in:
		-RandRecordSet.createTrainingSet() - changed the fixed values to variables.
		-RandRecordSet.getLeftovers() - removed "int dec" from parameters.