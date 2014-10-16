#SelfOrganizingMap


|Since		|09/04/14		|
|----------:|--------------:|
|Written by |Daniel Swain Jr|

##An alteration to the original SelfOrganizingMap.

#SetCreator  		

Creates the test and trainingSet from the input file
using the configurations in the config file.
TestSets are created by randomly grabbing 10% of 
each record of each decision 10 times. TrainingSets
are then created by taking the leftover 90% of each of the TestSets. 
TrainingSets are then trained by sending records into
an matrix that begins empty. When sent, 1 of 3 cases can happen:

* If the record sent is a neighbor to any of the rules
 in the matrix, the record updates the rule using an 
 algorithm and then added to the cluster of said rule.

* If the record is a neighbor to more than one rule, the decision
 of the record is used to decide which rule is updated by the 
 record. If they match, the rule is updated by the record.

* If the record is a neighbor to none of the rules, then 
 the record itself becomes a rule and is appended to the end
 of the matrix.

This is how trainingSets are created.

##Intersector

Takes the testSets and sends them to the corresponding 
trained trainingSet.
