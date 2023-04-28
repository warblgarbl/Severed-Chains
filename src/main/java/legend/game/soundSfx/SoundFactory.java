package legend.game.soundSfx;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import legend.game.unpacker.FileData;
import legend.game.unpacker.Unpacker;
import legend.game.unpacker.UnpackerException;

import java.util.List;

public final class SoundFactory {

  static BackgroundMusic backgroundMusic(final int fileIndex) {
    //TODO use Scus94491BpeSegment#loadDrgnDir
    final List<FileData> files = Unpacker.loadDirectory("SECT/DRGN0.BIN/" + fileIndex);

    final SshdParser sshdParser;
    final Sssq sssq;
    switch(files.size()) {
      case 4 -> {
        sshdParser = new SshdParser(files.get(2));

        sssq = new Sssq(files.get(1), sshdParser.createSoundFont(files.get(3)));
      }
      case 5 -> {
        final int soundbankCount = files.get(1).readUShort(0);
        final FileData[] soundbanks = new FileData[soundbankCount];

        soundbanks[0] = files.get(4);

        int totalSoundbankSize = soundbanks[0].size();
        for(int i = 1; i < soundbankCount; i++) {
          soundbanks[i] = Unpacker.loadFile("SECT/DRGN0.BIN/" + (fileIndex + i));
          totalSoundbankSize += soundbanks[i].size();
        }

        final byte[] allSoundbanks = new byte[totalSoundbankSize];
        int offset = 0;

        for(final FileData soundbank : soundbanks) {
          soundbank.copyFrom(0, allSoundbanks, offset, soundbank.size());
          offset += soundbank.size();
        }

        sshdParser = new SshdParser(files.get(3));

        sssq = new Sssq(files.get(2), sshdParser.createSoundFont(new FileData(allSoundbanks)));
      }
      default -> throw new RuntimeException("Unknown BGM type. File count " + files.size());
    }

    return new BackgroundMusic(sssq, sshdParser.getBreathControls());
  }

  static BackgroundMusic soundEffect(final String path) {
    //TODO use Scus94491BpeSegment#loadDrgnDir
    final List<FileData> files = Unpacker.loadDirectory(path);

    final SshdParser sshdParser = new SshdParser(files.get(2));

    final SoundFont soundFont = sshdParser.createSoundFont(files.get(3));

    final Sssq sssq = sshdParser.getSssq(soundFont);

    return new BackgroundMusic(sssq, sshdParser.getBreathControls());
  }


  private static class SshdParser {
    /**
     * <ul>
     *   <li>Index 0 - Instruments</li>
     *   <li>Index 1 - Volume Ramp??</li>
     *   <li>Index 2 - Breath Control wave</li>
     *   <li>Index 3 - Multi-Sequence</li>
     *   <li>Index 4 - Channels + Instruments (SFX)</li>
     * </ul>
     */
    private final Int2ObjectMap<FileData> subfiles = new Int2ObjectArrayMap<>();
    private int instrumentsSubfileIndex = 0;


    SshdParser(final FileData sshdData) {
      if(sshdData.readInt(12) != 0x64685353) {
        throw new UnpackerException("Not a SShd file!");
      }

      int lastOffset = sshdData.size();
      for(int i = 23;  i >= 0; i--) {
        final int offset = sshdData.readInt(16 + i * 4);

        if(offset != - 1) {
          this.subfiles.put(i, sshdData.slice(offset, lastOffset - offset));
          lastOffset = offset;
        }
      }
    }

    private Sssq getSssq(final SoundFont soundFont) {
      return new Sssq(this.subfiles.get(4), Sequence.getMultiSequence(this.subfiles.get(3)), soundFont);
    }

    private SoundFont createSoundFont(final FileData soundBankData) {
      if(this.subfiles.containsKey(0)) {
        //TODO should include volume ramp?? and breath control wave
        return new SoundFont(this.subfiles.get(0), new SoundBank(soundBankData));
      }

      return new SoundFont(this.subfiles.get(4).slice(0x190), new SoundBank(soundBankData));
    }

    private byte[][] getBreathControls() {
      if(!this.subfiles.containsKey(2)) {
        return new byte[0][];
      }

      final FileData file = this.subfiles.get(2);

      final int upperBound = file.readUShort(0);
      final byte[][] breathControls = new byte[upperBound + 1][];

      int nextOffset = file.size();

      for(int i = upperBound; i >= 0; i--) {
        final int startOffset = file.readUShort(2 + i * 2);
        breathControls[i] = file.slice(startOffset, nextOffset - startOffset).data();

        nextOffset = startOffset;
      }

      return breathControls;
    }
  }
}
