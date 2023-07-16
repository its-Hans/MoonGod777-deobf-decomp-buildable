package me.moon.event.events;

import me.moon.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class NoRenderEvent extends EventStage {
   public NoRenderEvent(int a) {
      super(a);
   }
}
