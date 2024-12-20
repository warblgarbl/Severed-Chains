package legend.game.types;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import legend.game.inventory.Equipment;
import legend.game.scripting.PartyFlagCategory;
import org.legendofdragoon.modloader.registries.RegistryId;
import legend.game.modding.coremod.CoreMod;

import java.util.EnumMap;
import java.util.Map;

import static legend.core.GameEngine.CONFIG;

public class CharacterData2c {
  public int xp_00;
  /**
   * <ul>
   *   <li>0x1 - in party</li>
   *   <li>0x2 - can be put in main party (without this flag a char can only appear in secondary and can't be put into main)</li>
   *   <li>0x20 - can't remove (don't select, can't be taken out of main party)</li>
   *   <li>0x40 - ultimate addition unlocked</li>
   * </ul>
   */
  public int partyFlags_04;
  public int hp_08;
  public int mp_0a;
  public int sp_0c;
  public int dlevelXp_0e;
  /** i.e. poison */
  public int status_10;
  public int level_12;
  public int dlevel_13;
  /** Only used during loading */
  public final Object2IntMap<EquipmentSlot> equipmentIds_14 = new Object2IntOpenHashMap<>();
  /** Only used during loading */
  public final Map<EquipmentSlot, RegistryId> equipmentRegistryIds_14 = new EnumMap<>(EquipmentSlot.class);
  public final Map<EquipmentSlot, Equipment> equipment_14 = new EnumMap<>(EquipmentSlot.class);
  public int selectedAddition_19;
  public final int[] additionLevels_1a = new int[8];
  public final int[] additionXp_22 = new int[8];

  public void set(final CharacterData2c other) {
    this.xp_00 = other.xp_00;
    this.partyFlags_04 = other.partyFlags_04;
    this.hp_08 = other.hp_08;
    this.mp_0a = other.mp_0a;
    this.sp_0c = other.sp_0c;
    this.dlevelXp_0e = other.dlevelXp_0e;
    this.status_10 = other.status_10;
    this.level_12 = other.level_12;
    this.dlevel_13 = other.dlevel_13;
    this.equipment_14.clear();
    this.equipment_14.putAll(other.equipment_14);
    this.selectedAddition_19 = other.selectedAddition_19;
    System.arraycopy(other.additionLevels_1a, 0, this.additionLevels_1a, 0, this.additionLevels_1a.length);
    System.arraycopy(other.additionXp_22, 0, this.additionXp_22, 0, this.additionXp_22.length);
  }

  public void setPartyFlags_04(final int val) {
    int inParty = val & 0x1;
    int canFight = val & 0x2;
    int previouslyJoined = val & 0x4;
    final int mustFight = val & 0x20;
    final int finalAddition = val & 0x40;
    final PartyFlagCategory flagConfig = CONFIG.getConfig(CoreMod.PARTY_FLAG_MODE_CONFIG.get());

    switch(flagConfig) {
      case PartyFlagCategory.BFF -> {
        inParty = inParty > 0 ? 0x1 : this.partyFlags_04 & 0x1;
        canFight = canFight > 0 ? 0x2 : this.partyFlags_04 & 0x2;
      }
      case PartyFlagCategory.FULL -> {
        inParty = 0x1;
        canFight = 0x2;
      }
    }

    if(inParty == 0 && (this.partyFlags_04 & 0x2) > 0) {
      previouslyJoined = 0x4;
    }

    this.partyFlags_04 = inParty | canFight | previouslyJoined | mustFight | finalAddition;
  }
}
