package legend.game.submap;

public enum SubmapState {
  INIT_0,
  LOAD_NEWROOT_1,
  WAIT_FOR_NEWROOT_2,
  LOAD_ENVIRONMENT_3,
  WAIT_FOR_ENVIRONMENT,
  CHANGE_SUBMAP_4,

  START_LOADING_MEDIA_10,
  FINISH_LOADING_AND_FADE_IN_11,
  RENDER_SUBMAP_12,
  LOAD_MENU_13,
  RENDER_MENU_14,
  UNLOAD_MENU_15,

  TRANSITION_TO_SUBMAP_17,
  TRANSITION_TO_WORLD_MAP_18,
  TRANSITION_TO_COMBAT_19,
  TRANSITION_TO_TITLE_20,
  TRANSITION_TO_FMV_21,
}
