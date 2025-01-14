require "torch"
require "nn"
require "math"

-- global variables
learningRate = 0.05
EPOCHS = 5


-- here we set up the architecture of the neural network
function create_network(nb_outputs)
	local numberOfFeatureMaps = 2
	local kernelSize = 7
	local neuronsRow = 23
	local subsamplingSize = 2
	local subsamplingStep = 2
	local convResult = neuronsRow - (kernelSize-1)
	local subSamplingRes = math.floor((convResult-subsamplingSize) / subsamplingStep)+1

   local ann = nn.Sequential();  -- make a multi-layer structure
   
   ann:add(nn.SpatialConvolution(1,numberOfFeatureMaps,kernelSize,kernelSize))  

   ann:add(nn.SpatialSubSampling(numberOfFeatureMaps,subsamplingSize,subsamplingSize,subsamplingStep,subsamplingStep))	
      
   ann:add(nn.Reshape(subSamplingRes*subSamplingRes*numberOfFeatureMaps)) --becames 3*3*6 vector of neurons
   
   ann:add(nn.Tanh()) --apply ativation function

   ann:add(nn.Linear(subSamplingRes*subSamplingRes*numberOfFeatureMaps,nb_outputs)) --fully connected layer
   
   --Torch initializes the weights with a uniform distribution that takes into account the fanin/fanout of the model. 

   return ann
end

-- train a Neural Netowrk
function train_network( network, dataset,datasetClasses)
               
   local criterion = nn.MSECriterion()
   
   for iteration=1,EPOCHS do --for maxIteration times
	   for index = 1,table.getn(dataset) do -- for each data in the training      
	      --print(table.getn(dataset) )
        	local input = dataset[index]               
         local output = datasetClasses[index]
         --print(input)
         --print(output)
       	criterion:forward(network:forward(input), output)
--         print(network.output)
         if(math.abs(output[1]-network.output[1]) < 0.1 and math.abs(output[2]-network.output[2] )< 0.1 and
         math.abs(output[3]-network.output[3]) < 0.1 and math.abs(output[4]-network.output[4]) < 0.1) then
        --print('--success!--')else
        --print('---NOT success--')
         end
         
       	network:zeroGradParameters() --reset parameters before backward call
        	network:backward(input, criterion:backward(network.output, output))
      	network:updateParameters(learningRate)
     	 end
    end

end

function test_predictor(predictor, test_dataset,testing_target_classes, classes)

     local mistakes = 0
     local tested_samples = 0
     
     print( "Prediction" )
     for i=1,table.getn(test_dataset) do

      local input  = test_dataset[i]
      local class_id = testing_target_classes[i]
      
      local responses_per_class  =  predictor:forward(input) 
      local probabilites_per_class = torch.exp(responses_per_class)
      local probability, prediction = torch.max(probabilites_per_class, 1) 
      
      --print(responses_per_class)
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
     print ( "Test success " .. 1-test_err )
	return mistakes
end


-- main routine
function main()
   local classes={1,2,3,4} 
   
   dofile('utility.lua')
   --load testset
   test_dataset = loadData('./../pythonScripts/data/testData.csv')
   test_target_classed = loadLabels('./../pythonScripts/data/testLabels.csv')
   
   for i=1,10 do
      print('------------------------------------------Training percentage '..(i*10))
      training_dataset= loadData('./../pythonScripts/data/trainingData'..(i*10)..'.csv')
      training_target_classes  = loadLabels('./../pythonScripts/data/trainingLabels'..(i*10)..'.csv')
      --create net
      local network = create_network(#classes) --#classes is the number of classes

      --train
      local nClock = os.clock()
      train_network(network, training_dataset, training_target_classes)
      print('Elapsed time is '.. os.clock()-nClock)
      --test
      test_predictor(network, test_dataset, test_target_classed,classes)
   end


end

----------------------call Main
main()
