require "torch"
require "nn"
require "math"

-- global variables
learningRate = 0.012
maxIterations = 5


-- here we set up the architecture of the neural network
function create_network(nb_outputs)
	local numberOfFeatureMaps = 3
	local kernelSize = 5
	local neuronsRow = 23
	local convResult = neuronsRow - (kernelSize-1)
	local subSamplingRes = math.floor(convResult / 2)
	print(subSamplingRes)
   local ann = nn.Sequential();  -- make a multi-layer structure
   
   -- input is 8x8x1                        
   ann:add(nn.SpatialConvolution(1,numberOfFeatureMaps,kernelSize,kernelSize))  
--becames 6x6x6 
-- numberOfInputPlane, numberOfOutputPlane,kernelWidth,kernelHeight
--the default step of convolution is 1
--padding can be added
   ann:add(nn.SpatialSubSampling(numberOfFeatureMaps,2,2,2,2))
	--ann:add(nn.SpatialAveragePooling(2,2,2,2))
-- becomes  3x3x6
--numberOfinputPlanes,kernelWidth,kernelHeight,stepOfSubsamplingWidth,stepOfSubsamplingHeight

--i can try also nn.SpatialAveragePooling(kW,kH)
      
   ann:add(nn.Reshape(subSamplingRes*subSamplingRes*numberOfFeatureMaps)) --becames 3*3*6 vector of neurons
   ann:add(nn.Tanh()) --apply ativation function

   ann:add(nn.Linear(subSamplingRes*subSamplingRes*numberOfFeatureMaps,nb_outputs)) --fully connected layer
   
   --Torch initializes the weights with a uniform distribution that takes into account the fanin/fanout of the model. 

   return ann
end

-- train a Neural Netowrk
function train_network( network, dataset,datasetClasses)
               
   print( "Training the network" )
   local criterion = nn.MSECriterion()
   
   for iteration=1,maxIterations do --for maxIteration times
	   for index = 1,table.getn(dataset) do -- for each data in the training      
	      --print(table.getn(dataset) )
        	local input = dataset[index]               
         local output = datasetClasses[index]
         --print(input)
         --print(output)
       	criterion:forward(network:forward(input), output)
         
       	network:zeroGradParameters() --reset parameters before backward call
        	network:backward(input, criterion:backward(network.output, output))
      	network:updateParameters(learningRate)
     	 end
    end

end

function test_predictor(predictor, test_dataset,testing_target_classes, classes)

     local mistakes = 0
     local tested_samples = 0
     
     print( "----------------------" )
     print( "Prediction" )
     for i=1,table.getn(test_dataset) do

      local input  = test_dataset[i]
      local class_id = testing_target_classes[i]
      
      local responses_per_class  =  predictor:forward(input) 
      local probabilites_per_class = torch.exp(responses_per_class)
      local probability, prediction = torch.max(probabilites_per_class, 1) 
      
             
      if class_id[prediction[1]] ~= 1 then
          mistakes= mistakes + 1
          local label = class_id[prediction]
          local predicted_label = prediction
         -- print(i,prediction,class_id,class_id[prediction] )--, predicted_label )
	   else		   
	      --local label = classes_names[ classes[class_id] ]
         --local predicted_label = classes_names[ classes[prediction[1] ] ]
         --print(i , label , predicted_label )
      end

     tested_samples = tested_samples + 1
     end

     print("mistakes " .. mistakes .." on " .. tested_samples)
     local test_err = mistakes/tested_samples
     print ( "Test error " .. test_err )
	return mistakes
end


function trainAndTest(network, training_dataset, training_target_classes,training_correct_class_id, classes, classes_names,testing_dataset,testing_target_classes)	
        
	local mistakes = torch.Tensor(testing_dataset:size(1)):fill(0)
	local mistakesTraining = torch.Tensor(training_dataset:size(1)):fill(0)
	local total_tests = 5

	for i = 1,total_tests do		
	        train_network(network, training_dataset, training_target_classes)
        
		mistakesTraining = mistakesTraining + test_predictor(network, training_dataset, training_correct_class_id,classes, classes_names)
    		mistakes = mistakes + test_predictor(network, testing_dataset,testing_target_classes,classes, classes_names)
	end
	
	for i=1, training_dataset:size(1) do
		local test_err_train = mistakesTraining[i]/(total_tests*9) * 100
		print  ( "Test error training " .. 100 - test_err_train .. " ( " .. mistakesTraining[i] .. " out of " .. total_tests*9 .. " )")
	end

	for i=1, testing_dataset:size(1) do
		local test_err = mistakesTraining[i]/(total_tests*3) * 100
		print  ( "Test error test " .. 100 - test_err .. " ( " .. mistakes[i] .. " out of " .. total_tests*3 .. " )")
	end


	
end

function getStats(network, training_dataset, training_target_classes)
	for i=1,10 do
	 train_network(network, training_dataset, training_target_classes)
	end
end

-- main routine
function main()

local classes = {1,2,3,4}
      
local training_dataset, 
training_target_classes,
classes = dofile('loadCsv.lua')

--create net
local network = create_network(#classes) --#classes is the number of classes

--trainAndTest(network, training_dataset, training_target_classes,training_correct_class_id, classes, classes_names,testing_dataset,testing_target_classes)

train_network(network, training_dataset, training_target_classes)
test_predictor(network, training_dataset, training_target_classes,classes)
--print ("Robustness test")
--robustnessTest(network, training_dataset, training_correct_class_id, classes, classes_names)

--getStats(network, training_dataset,training_target_classes)

end


main()
