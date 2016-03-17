# sNNake
Snake game controlled by an artificial neural network

## Quickstart

Once you have cloned the repository,

1. In command line from project root, run:  
```
cd SnakeBrain
python server.py
```
  
2. Import ./Snake/ into IntelliJ IDEA  
Make sure `IS_TRAINING = false` in `Config.java`.

3. Run `Snake.java` and watch the snake eat apples by itself.

## Training

To train the snake (assuming you have completed quickstart):

1. Change `IS_TRAINING` to `true` in `Config.java`.

2. Run `Snake.java`. Use the arrow keys to move the snake.  
When training is complete, press 'E' to export the training data.

3. Look in ./TrainingData/ and find the .csv file you want to use to train the snake. Copy this file into ./TrainingData/active/. Make note of the vision type. You are only generating parameters for this vision type. Parameters for other vision types will be unaffected.

4. Open config.py and change `import config_visionX as cfg` to the correct vision type. For example, if you wanted to generate parameters for vision1, you would change it to `import config_vision1 as cfg`.

5. Run `snakecharmer.py` (assuming you are already in ./SnakeBrain/):  
```
python snakecharmer.py
```

## Using Training Results

1. Look in ./Parameters/ for the newly generated .csv file. Copy that to ./Parameters/active/ and rename to `weights_visionX.csv`, where visionX corresponds to the vision type.

2. Restart the Python server (`server.py`)

3. Make sure `VISION_TYPE` in `Config.java` is set to the correct vision type. For example, if you trained for vision1, then set it to `Example.VISION_1`.

4. Change `IS_TRAINING` in `Config.java` back to `false`

5. Run `Snake.java` and watch your newly trained snake eat apples by itself.

## Notes

* There are total 3 vision types, creatively named vision1, vision2, vision3.

* Different vision types determine what the snake sees. Play around with them until you find one that works the best. Then let me know (please?)

* You should know that vision1 sucks. A lot. Unless your board is less than 10x10, don't use it.

* I'm have not yet determined which is better, vision2 or vision3. They both work pretty well.

* I'm not a snake master. Thus snakes that learn from me are not masters themselves. The included trained parameters will get your snake to a score of around 30, 40 if you're lucky, and 50 if you're really lucky.

* If you have trained this snake to do better, please submit a pull request :)
