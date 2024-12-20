package legend.game.modding.coremod.config;

import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;
import legend.game.scripting.ActivePartyCategory;

public class ActivePartyConfigEntry extends EnumConfigEntry<ActivePartyCategory> {
  public ActivePartyConfigEntry() {
    super(ActivePartyCategory.class, ActivePartyCategory.RETAIL, ConfigStorageLocation.CAMPAIGN, ConfigCategory.PARTY);
  }
}
