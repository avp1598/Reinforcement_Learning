import gym
import gym_ple

import skimage as skimage
from skimage import transform, color, exposure
from skimage.transform import rotate
from skimage.viewer import ImageViewer

import random
import numpy as np

from collections import deque

import keras
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation, Flatten
from keras.layers.convolutional import Convolution2D, MaxPooling2D
from keras.optimizers import SGD , Adam
from keras.utils.np_utils import to_categorical


  	

env = gym.make("FlappyBird-v0")
observation = env.reset()


def prepro(observation,prev):
    x_t1 = skimage.color.rgb2gray(observation)
    x_t1 = skimage.transform.resize(x_t1,(80,80))
    x_t1 = skimage.exposure.rescale_intensity(x_t1, out_range=(0, 255))

    x_t1 = x_t1.reshape(1, x_t1.shape[0], x_t1.shape[1],1) #1x80x80x1
    s_t1 = x_t1-prev
    return s_t1

prev_x = None 
running_reward = None
reward_sum = 0
episode_number = 0
exp_replay=deque()
img_rows , img_cols = 80, 80
replay_length=1000

prev=0

ep=0
while ep<100:
	_=env.reset()
	for _ in range(1000):

		action=env.action_space.sample()
	  	observation,reward,done,_ = env.step(action)
		state = prepro(observation,prev)
		prev=state
		if(len(exp_replay))>replay_length: exp_replay.pop()
		exp_replay.append((state,action,reward))
		if done: 
			ep+=1
			break

model = Sequential()
model.add(Convolution2D(32, 8,8,subsample=(4,4),border_mode='same',input_shape=(img_cols,img_rows,1)))
model.add(Activation('relu'))
model.add(Convolution2D(64, 4, 4, subsample=(2, 2), border_mode='same'))
model.add(Activation('relu'))
model.add(Convolution2D(64, 3, 3, subsample=(1, 1), border_mode='same'))
model.add(Activation('relu'))
model.add(Flatten())
model.add(Dense(512))
model.add(Activation('relu'))
model.add(Dense(2, activation='softmax'))
adam=keras.optimizers.Adam(lr=0.00001)
model.compile(loss='categorical_crossentropy', optimizer='adam')


def train(model,exp_replay):
	for i in range(10):

		minibatch = np.array(random.sample(exp_replay,100))
		state_t=[]
		action_t=[]
		reward_t=[]
		for j in range(100):
			state_t.append(minibatch[j][0])
			action_t.append(minibatch[j][1])
			reward_t.append(minibatch[j][2])

		state_t=np.array(state_t).reshape(100,80,80,1)
		action_t = to_categorical(np.array(action_t))
    	reward_t = np.array(reward_t)
       	advantage = reward_t - reward_t.mean()
    	model.fit(state_t, action_t,batch_size=100,sample_weight=advantage, epochs=1)

train(model,exp_replay)

ep=0
epsilon=0.5
while ep<20000:
	observation=env.reset()
	state=prepro(observation,0)
	for _ in range(1000):
		if(ep>15000):env.render()
		action=model.predict(state)
		if(action[0][0]==1): action=0
		else: action=1
		if(np.random.uniform()>epsilon): action=1-action
	  	observation,reward,done,_ = env.step(action)
		state = prepro(observation,prev)
		prev=state
		if(len(exp_replay))>replay_length: exp_replay.popleft()
		exp_replay.append((state,action,reward))
		if done:
			ep+=1
			break
	if ep%250==0 and ep!=0:
		train(model,exp_replay)
		epsilon+=0.01
