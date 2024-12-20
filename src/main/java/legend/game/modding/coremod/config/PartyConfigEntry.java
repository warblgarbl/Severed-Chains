package legend.game.modding.coremod.config;

import legend.game.SItem;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.PartySettingsScreen;
import legend.game.inventory.screens.controls.Button;
import legend.game.modding.coremod.CoreMod;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

import java.util.Set;

import static legend.game.Scus94491BpeSegment.startFadeEffect;

public class PartyConfigEntry extends ConfigEntry<Void> {
  public PartyConfigEntry() {
    super(null, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, o -> new byte[0], bytes -> null);

    this.setEditControl((current, configCollection) -> {
      final Button button = new Button(I18n.translate(CoreMod.MOD_ID + ".config.party_settings.configure"));
      button.onPressed(() -> button.getScreen().getStack().pushScreen(new PartySettingsScreen(configCollection, Set.of(ConfigStorageLocation.CAMPAIGN), ConfigCategory.PARTY, () -> {
        startFadeEffect(2, 10);
        SItem.menuStack.popScreen();
      })));

      return button;
    });
  }
}
