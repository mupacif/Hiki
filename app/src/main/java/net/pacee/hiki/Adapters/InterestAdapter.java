package net.pacee.hiki.Adapters;


import net.pacee.hiki.Model.Interest;

import java.util.List;

/**
 * Created by mupac_000 on 20-11-17.
 */

public interface InterestAdapter  {
    void setEventListener(EventListener listener);
    void setInterests(List<Interest> interests);
}
