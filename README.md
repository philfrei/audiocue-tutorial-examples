# audiocue-tutorial-examples

[`AudioCue`](https://github.com/philfrei/AudioCue-maven) is a Java class for audio 
playback from memory, modeled on the `javax.sound.sampled.Clip` class, but with 
concurrent playback and built-in real-time controls for volume, pan and speed.

This library provides a collection of programs which demonstrate many of *AudioCue's* 
capabilities. This project's `pom.xml` shows how to link the `AudioCue` library 
using Maven. More info on how to obtain and configure `AudioCue` can be found in 
the README for the [AudioCue project](https://github.com/philfrei/AudioCue-maven).

There are four sections. Each contains a program (or two) with its own `main()`.

###1. demo1_playback_basics

**SimplePlayback** : This example shows the playing of a cue in a fire-and-forget 
manner. The play command is triggered from a `JButton`. The sound is configured 
to allow up to 6 concurrent plays.

**RealTimeslidersCue** : This example has two controls that make use of the same 
audio asset: a bell pitched at A3 (220 Hz). The upper control is a `JLabel` that 
plays the sound in response to a `MouseDown` event. Each play grabs an available 
instance and assigns it a random volume, pan and speed. The lower control is a 
`JButton`. This control makes use of a single, reserved instance that has been 
obtained from the `AudioCue`. Below are three sliders that affect playback in 
real time: one for volume, one for pan, one for speed.

##2. demo2_listener

**AudioCueListenerDemo** : In this example a button plays the sound of a crow cawing 
a random number of times (from 1 to 4). The graphic used for the face of the button
displays one of two images of a crow. When the `AudioCue` is started, the image is 
changed from a quiet crow to one that is cawing. When the `AudioCue` finishes looping,
the image reverts to the quiet crow. A `JFrame` implements `AudioCueListener` and
is registered as a Listener via *AudioCue's* `addAudioCueListener` method.

##3. demo3_pcm

**PCMDemo** : In this example, two `AudioCues` are loaded directly with PCM data. The
first is given filtered pink noise that is read from a wav file created using 
[Audacity](https://www.audacityteam.org/). The data is then given a simple Attack-Release
envelope. With the second `AudioCue`, a sawtooth wave is constructed from sine 
waves. The PCM values for 32 harmonics are calculated using a sine function and summed 
together.

##4. demo4_soundscape

**SoundscapeBattlefield** : This example shows how a single cue can be used multiple
ways to create a sonic landscape. The source of the `AudioCue` is a wav file of a 
single gunshot. This shot, when played at low speeds, sounds much like a bomb explosion.
The cue has a long, reverberant tail. In the demo, a copy of the PCM of the gunshot is 
obtained and edited into a much shorter, loopable cue. This is then loaded into a new
`AudioCue` that is used to create the sound of a machine gun. The example also shows 
two possible ways in which one can associate an `AudioCue` with properties or methods 
to create the illusion of an 'entity' with a location and behavioral tendencies. Also, 
the demo includes a class which implements `AudioMixerTrack` and is used to play 
a sound effect of a helicopter flyover. An `AudioMixer` merges three `AudioMixerTracks`: 
one for the helicopter, one for the machine guns, and one that is used for both the bombs 
and single-shot rifles.

