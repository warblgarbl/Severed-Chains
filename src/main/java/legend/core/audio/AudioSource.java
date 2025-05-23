package legend.core.audio;

import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceQueueBuffers;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourceUnqueueBuffers;
import static org.lwjgl.system.MemoryUtil.memFree;

public abstract class AudioSource {
  private final int[] buffers;
  private int bufferIndex;
  private int sourceId;

  private boolean playing;

  private IntBuffer tmp;

  public AudioSource(final int bufferCount) {
    this.buffers = new int[bufferCount];
  }

  protected boolean isInitialized() {
    return this.sourceId != 0;
  }

  protected void init() {
    this.sourceId = alGenSources();
    this.tmp = MemoryUtil.memAllocInt(1);

    alGenBuffers(this.buffers);
    this.bufferIndex = this.buffers.length - 1;
  }

  protected void destroy() {
    this.playing = false;
    alSourceStop(this.sourceId);

    alGetSourcei(this.sourceId, AL_BUFFERS_PROCESSED, this.tmp);
    final int processedBufferCount = this.tmp.get(0);

    for(int buffer = 0; buffer < processedBufferCount; buffer++) {
      final int processedBufferName = alSourceUnqueueBuffers(this.sourceId);
      alDeleteBuffers(processedBufferName);
    }

    alDeleteBuffers(this.buffers);
    alDeleteSources(this.sourceId);

    memFree(this.tmp);

    Arrays.fill(this.buffers, 0);
    this.sourceId = 0;
    this.tmp = null;
  }

  public void tick() {
    // Restart playback if stopped
    if(this.isPlaying()) {
      this.play();
    }
  }

  public boolean canBuffer() {
    if(!this.playing || !this.isInitialized()) {
      return false;
    }

    return this.bufferIndex >= 0;
  }

  protected void handleProcessedBuffers() {
    if(this.bufferIndex < this.buffers.length - 1) {
      alGetSourcei(this.sourceId, AL_BUFFERS_PROCESSED, this.tmp);
      final int processedBufferCount = this.tmp.get(0);

      for(int buffer = 0; buffer < processedBufferCount; buffer++) {
        this.buffers[++this.bufferIndex] = alSourceUnqueueBuffers(this.sourceId);
      }
    }
  }

  protected void bufferOutput(final int format, final short[] buffer, final int sampleRate) {
    synchronized(this) {
      if(this.bufferIndex >= 0) {
        final int bufferId = this.buffers[this.bufferIndex--];
        alBufferData(bufferId, format, buffer, sampleRate);
        alSourceQueueBuffers(this.sourceId, bufferId);
      }
    }
  }

  protected void play() {
    alGetSourcei(this.sourceId, AL_SOURCE_STATE, this.tmp);
    if(this.tmp.get(0) != AL_PLAYING) {
      alSourcePlay(this.sourceId);
    }
  }

  protected void stop() {
    this.playing = false;

    if(this.isInitialized()) {
      alSourceStop(this.sourceId);
    }
  }

  protected void setPlaying(final boolean playing) {
    this.playing = playing;
  }

  public boolean isPlaying() {
    return this.playing;
  }

  protected void resetBuffers() {
    alSourceStop(this.sourceId);

    final int processed = alGetSourcei(this.sourceId, AL_BUFFERS_PROCESSED);

    for (int i = 0; i < processed; i++) {
      alSourceUnqueueBuffers(this.sourceId, this.buffers);
      alDeleteBuffers(this.buffers[0]);
    }

    alGenBuffers(this.buffers);
    this.bufferIndex = this.buffers.length - 1;

    if(this.playing) {
      alSourcePlay(this.sourceId);
    }
  }
}
