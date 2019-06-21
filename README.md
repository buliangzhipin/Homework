# Homework
各種宿題のgit
# NatCorder API
NatCorder is a lightweight, easy-to-use, native video recording API for iOS and Android. NatCorder comes with a rich featureset including:
+ Record any texture or anything that can be rendered into a texture.
+ Record to MP4 videos and animated GIF images.
+ Control recording quality and file size with bitrate and keyframe interval.
+ Record at any resolution. You get to specify what resolution recording you want.
+ Get path to recorded video in device storage.
+ Record game audio with video.
+ Support for recording on macOS in the Editor or in Standalone builds.
+ Support for recording on Windows in the Editor or in Standalone builds.
+ Experimental support for recording on WebGL.
+ Experimental support for recording HEVC videos.

## Fundamentals of Recording
NatCorder provides a simple recording API with instances of the `IMediaRecorder` interface. **NatCorder works by encoding video and audio frames on demand**. To start recording, simply create a recorder corresponding to the media type you want to record:
```csharp
var gifRecorder = new GIFRecorder(...);
var videoRecorder = new MP4Recorder(...);
```

Once you create a recorder, you then commit frames to it. You can commit video and audio frames to these recorders. These committed frames are then encoded into a media file. When committing frames, you must provide the frame data with a corresponding timestamp. The spacing between timestamps determine the final frame rate of the recording.

### Committing Video Frames
NatCorder records video using `Texture`s. When committing a `Texture` for encoding, you will need to provide a corresponding timestamp. For this purpose, you can use implementations of the `IClock` interface. Here is an example illustrating recording a `WebCamTexture`:
```csharp
WebCamTexture webcamPreview;
IMediaRecorder mediaRecorder;
IClock clock;

void StartRecording () {
    // Start the webcam preview
    webcamPreview = new WebCamTexture(...);
    webcamPreview.Play();
    // Start recording
    clock = new RealtimeClock();
    mediaRecorder = new MP4Recorder(...) or GIFRecorder(...) or HEVCRecorder(...);
}

void Update () {
    // Check that we are recording
    if (mediaRecorder != null)
        mediaRecorder.CommitFrame(webcamPreview, clock.Timestamp);  // Commit the frame to the recorder
}

void StopRecording () {
    // Stop recording
    mediaRecorder.Dispose();
    mediaRecorder = null;
}
```

### Committing Audio Frames
NatCorder records audio provided as interleaved PCM sample buffers (`float[]`). Similar to recording video frames, you will call the `IMediaRecorder.CommitSamples` method, passing in a sample buffer and a corresponding timestamp. It is important that the timestamps synchronize with those of video, so it is recommended to use the same `IClock` for generating video and audio timestamps. Below is an example illustrating recording game audio using Unity's `OnAudioFilterRead` callback:
```csharp
void OnAudioFilterRead (float[] data, int channels) {
    // Check that we are recording
    if (mediaRecorder != null)
        // Commit the audio frame
        mediaRecorder.CommitSamples(data, clock.Timestamp);
}
```

## Easier Recording with Recorder Inputs
In most cases, you will likely just want to record a game camera optionally with game audio. To do so, you can use NatCorder's recorder `Inputs`. A recorder `Input` is a lightweight utility class that eases out the process of recording some aspect of a Unity application. NatCorder comes with two recorder inputs: `CameraInput` and `AudioInput`. You can create your own recorder inputs to do more interesting things like add a watermark to the video, or retime the video. Here is a simple example showing recording a game camera:
```csharp
IMediaRecorder mediaRecorder;
CameraInput cameraInput;
AudioInput audioInput;

void StartRecording () {
    // Start recording
    mediaRecorder = new ...;
    // Create a camera input to record the main camera
    cameraInput = new CameraInput(mediaRecorder, Camera.main);
    // Create an audio input to record the scene's AudioListener
    audioInput = new AudioInput(mediaRecorder, audioListener);
}

void StopRecording () {
    // Destroy the recording inputs
    cameraInput.Dispose();
    audioInput.Dispose();
    // Stop recording
    mediaRecorder.Dispose();
    mediaRecorder = null;
}
```

___

## Limitations of the WebGL Backend
The WebGL backend is currently experimental. As a result, it has a few limitations in its operations. Firstly, it is an 'immediate-encode' backend. This means that video frames are encoded immediately they are committed. As a result, there is no support for custom frame timing (the `timestamp` provided to `CommitFrame` is always ignored).

Secondly, because Unity does not support the `OnAudioFilterRead` callback on WebGL, we cannot record game audio on WebGL (using an `AudioSource` or `AudioListener`). This is a limitation of Unity's WebGL implementation. However, you can still record raw audio data using the `IMediaRecorder.CommitSamples` API.

The `MP4Recorder` may record videos with the VP8/9 codec or H.264 codec, depending on the browser and device. These videos are always recorded in the `webm` container format. The `GIFRecorder` is not supported on WebGL.

## Using NatCorder with NatCam
If you use NatCorder with our NatCam camera API, then you will have to remove a duplicate copy of the `NatRender.aar` library **from NatCam**. The library can be found at `NatCam > Plugins > Android > NatRender.aar`.

## Tutorials
- [Unity Recording Made Easy](https://medium.com/@olokobayusuf/natcorder-unity-recording-made-easy-f0fdee0b5055)
- [Audio Workflows](https://medium.com/@olokobayusuf/natcorder-tutorial-audio-workflows-1cfce15fb86a)

## Requirements
- Unity 2018.3+
- Android API Level 21+
- iOS 11+
- macOS 10.13+
- Windows 10+, 64-bit only
- WebGL:
    - Firefox 25+
    - Chrome 47+
    - Safari 27+

## Notes
- NatCorder doesn't support recording UI canvases that are in Screen Space - Overlay mode. See [here](https://forum.unity3d.com/threads/render-a-canvas-to-rendertexture.272754/#post-1804847).
- NatCorder requires the Metal graphics API on macOS and iOS, in the Editor and Standalone builds.
- When building for WebGL, make sure that 'Use Prebuild Engine' is disabled in Build Settings.
- When recording audio, make sure that the 'Bypass Listener Effects' and 'Bypass Effects' flags on your `AudioSource`s are turned off.
- If you face `DllNotFound` errors on standalone Windows builds, install the latest Visual C++ redistributable on the computer.

## Quick Tips
- Please peruse the included scripting reference [here](https://olokobayusuf.github.io/NatCorder-Docs/)
- To discuss or report an issue, visit Unity forums [here](https://forum.unity.com/threads/natcorder-video-recording-api.505146/)
- Contact me at [olokobayusuf@gmail.com](mailto:olokobayusuf@gmail.com)

Thank you very much!

## NatCorder 1.6.0
+ Added experimental support for HEVC with the `HEVCRecorder`. This is currently supported on iOS, macOS, and Windows.
+ The `IMediaRecorder.CommitFrame` method now accepts a pixel buffer instead of a `RenderTexture`. This means you can commit a `byte[]`, `Color32[]`, or any other pixel buffer array.
+ The `IMediaRecorder.AcquireFrame` method has been removed.
+ All `IMediaRecorder` implementations are now completely thread safe. As a result you can now record in a background thread, hence improving general performance.
+ Greatly improved recording performance and memory stability (no more GC!).
+ Added `pixelWidth` and `pixelHeight` getter properties to instances of `IMediaRecorder`.
+ Added WebCam example that demonstrates recording a `WebCamTexture` to a video.
+ Prevent crash when recording is stopped without committing frames. Instead, the recording callback will not be invoked.
+ Refactored `RealtimeClock.IsPaused` getter property to `RealtimeClock.Paused` read-write property.
+ Refactored `FixedIntervalClock.Advance` method to `FixedIntervalClock.Tick`.
+ Deprecated `RealtimeClock.Pause` and `RealtimeClock.Resume` methods.
+ Deprecated `FixedIntervalClock.AutoAdvance` property. You must now manually `Tick` the clock.
+ Deprecated GreyWorld example.
+ NatCorder now requires iOS 11+.
+ NatCorder now requires Windows 10 64-bit.
+ NatCorder now requires Metal in macOS Standalone builds and in the macOS Editor (enable this in Player Settings).
+ Reduced mimimum Android requirement to API level 21.

## NatCorder 1.5.1
+ Add support for multiple game cameras in `CameraInput`.
+ Fixed memory leak when recording on Windows.
+ Refactored `CameraInput` to use instance constructor instead of static factory method.
+ Refactored `AudioInput` to use instance constructor instead of static factory method.

## NatCorder 1.5.0
+ Completely overhauled front-end API to provide a more recorder-oriented approach. Check out the README.md for more info.
+ Added support for running multiple recording sessions simultaneously on devices that support this.
+ Fixed crash when recording was stopped on some Android devices.
+ Refactored NatCorder namespace from `NatCorderU.Core` to `NatCorder`.
+ Refactored recorder input namespace from `NatCorderU.Core.Recorders` to `NatCorder.Inputs`.
+ Refactored recording clock namespace from `NatCorderU.Core.Clocks` to `NatCorder.Clocks`.
+ Refactored `CameraRecorder` to `CameraInput`.
+ Refactored `AudioRecorder` to `AudioInput`.
+ Refactored `IClock.CurrentTimestamp` to `IClock.Timestamp`.
+ Deprecated `NatCorder` class.
+ Deprecated `RecordingCallback` delegate type.
+ Deprecated `Container` enum.
+ Deprecated `VideoFormat` struct.
+ Deprecated `AudioFormat` struct.
+ Deprecated `IRecorder` struct.

## NatCorder 1.4.1
+ Greatly improved memory stability on iOS.
+ Fixed crash when GIF recording is stopped on iOS.
+ Fixed crash when recording MP4 with audio on Android.

## NatCorder 1.4.0
+ Greatly improved recording stability and performance on Android. As a result, NatCorder now requires a minimum of API level 23.
+ Greatly improved recording stability and performance on iOS Metal. As a result, NatCorder now requires a minimum of iOS 8.
+ Greatly improved GIF visual quality on Android.
+ Dropped support for OpenGL ES on iOS. NatCorder will only use Metal on iOS. 
+ Added support for the new Lightweight Render Pipeline (LWRP) and High Definition Render Pipeline (HDRP) with `CameraRecorder`.
+ Update iOS and macOS backend to generate `.mp4` instead of `.mov` file when recording MP4.
+ Added `FixedIntervalClock` for generating timestamps to maintain a constant framerate in recorded videos.
+ Added aspect fitting in `CameraRecorder`. This will ensure that videos will not appear stretched in the case of app autorotation or uneven recording sizes.
+ Added support for linear rendering on macOS and Windows.
+ Added a dedicated GIF recording example called Giffy.
+ Fixed `BufferOverflowException` when recording with audio on some Android devices.
+ Fixed `DllNotFoundException` when running on macOS.
+ Deprecated `VideoFormat.Screen` property. Manually create a video format with your intended resolution.
+ Deprecated `CameraRecorder.recordingMaterial` property. Use Image Effects instead.
+ Removed GIF recording from ReplayCam.

## NatCorder 1.3f2
+ Added `IClock` interface and `RealtimeClock` class. Clocks are lightweight objects that generate extremely accurate timestamps for recording. They allow for audio to be perfectly synchronized with video when recording with audio.
+ Added full support for pausing and resuming recording with `Recorder` classes with the new `Clock` infrastructure.
+ Changed `NatCorder.CommitFrame` to take in a `RenderTexture` with a corresponding timestamp.
+ Fixed duplicate key error when `NatCorder.CommitFrame` is called on iOS.
+ Fixed `CameraRecorder` destroying recording material once recording is finished.
+ Fixed unallocated buffer exception being raised when recording with audio on iOS 12.
+ Fixed rare `NullPointerException` crash when recording MP4 on Android.
+ Deprecated `AudioRecorder.Create` overload that took in both an `AudioSource` and `AudioListener`.
+ Deprecated `Frame` class.

## NatCorder 1.3f1
+ Added GIF recording on iOS, Android, macOS, and Windows!
+ Added `Recorders` namespace, `VideoRecorder` component, and `AudioRecorder` component for quickly recording different gameobjects like cameras and audio sources.
+ Added proper support for offline recording, where a set of pre-rendered frames are all committed to NatCorder in one loop.
+ Added `Container` enumeration for specifying container format for recording (MP4 or GIF).
+ Added `VideoFormat` and `AudioFormat` structs for configuring recording.
+ Fixed crash on macOS and iOS when very short video (less than 1 second) is recorded.
+ Ensure that `IsRecording` properly changes immediately after `StartRecording` and `StopRecording`.
+ Improved speed of `RecordingCallback` being invoked on WebGL.
+ Refactored `VideoCallback` to `RecordingCallback`.
+ Deprecated `Replay` API.
+ Deprecated `IAudioSource` interface.
+ Deprecated `Configuration` struct.

## NatCorder 1.2f2
+ Improved recording stability on iOS.
+ File paths on iOS and macOS are no more prepended with the `file://` protocol.
+ Fixed tearing in recorded video on iOS Metal.
+ Fixed audio being slightly behind of video on iOS and macOS.
+ Fixed crash on Android when very short video (less than 1 second) is recorded.
+ Fixed null reference exception when recording is stopped on OSX, Windows, and WebGL.

## NatCorder 1.2f1
+ We have significantly improved recording performance in iOS apps, especially in apps using the Metal API.
+ The Windows backend is no more experimental! It is now fully supported.
+ When recording with the `Replay` API, aspect fitting will be applied to prevent stretching in the video.
+ We have deprecated the Sharing API because we introduced a dedicated sharing API, [NatShare](https://github.com/olokobayusuf/NatShare-API).
+ Fixed audio stuttering in recorded videos on Windows.
+ Fixed tearing in recorded video when using OpenGL ES on iOS.
+ Fixed rare crash when recording in app that uses Metal API on iOS.
+ Fixed tearing and distortion in recorded video on macOS.
+ Fixed microphone audio recording from an older time in ReplayCam example.
+ Fixed crash when user tries to save video to camera roll from sharing dialog on iOS.
+ Deprecated `Microphone` API. Use `UnityEngine.Microphone` instead.
+ Deprecated `NatCorder.Verbose` flag.

## NatCorder 1.1f1
+ We have added a native macOS backend! The NatCorder recording API is now fully supported on macOS.
+ We have also added a native Windows backend! This backend is still experimental so it should not be used in production builds.
+ The Standalone backend (using FFmpeg) has been deprecated because we have added native Windows and macOS implementations.
+ We have significantly improved recording stability on Android especially for GPU-bound games.
+ Added support for different Unity audio DSP latency modes.
+ Added `sampleCount` property in `IAudioSource` interface.
+ Fixed crash when `StartRecording` is called on iOS running OpenGL ES2 or ES3.
+ Fixed rare crash on Android when recording with audio.
+ Fixed audio-video timing discrepancies on Android.
+ Fixed video tearing on Android when app does not use multithreaded rendering.
+ Fixed `FileUriExposedException` when `Sharing.Share` is called on Android 24 or newer.
+ Fixed `Sharing.GetThumbnail` not working on iOS.
+ Fixed `Sharing.SaveToCameraRoll` failing when permission is requested and approved on iOS.
+ Fixed `Sharing.SaveToCameraRoll` not working on Android.
+ Fixed rare crash on Android when a very short video (less than 1 second) is recorded.
+ Fixed build failing due to missing symbols for Sharing library on iOS.
+ Improved on microphone audio stuttering in ReplayCam example by adding minimal `Microphone` API in `NatCorderU.Extensions` namespace.
+ Refactored `Configuration.Default` to `Configuration.Screen`.
+ *Everything below*

## NatCorder 1.0f1
+ First release
