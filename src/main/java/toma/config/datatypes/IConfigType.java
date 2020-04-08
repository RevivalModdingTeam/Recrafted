package toma.config.datatypes;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import toma.config.object.IConfigSerializable;
import toma.config.ui.ModConfigScreen;
import toma.config.ui.widget.ConfigWidget;

public interface IConfigType<T> extends IConfigSerializable {

    String displayName();

    T value();

    void set(T value);

    void onLoad();

    boolean hasComment();

    String getComment();

    @OnlyIn(Dist.CLIENT)
    ConfigWidget<T> create(int x, int y, int w, int h, ModConfigScreen screen);
}
