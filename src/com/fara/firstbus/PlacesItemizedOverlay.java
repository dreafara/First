package com.fara.firstbus;
 
import java.util.ArrayList;
 
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
 
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
 
public class PlacesItemizedOverlay extends ItemizedOverlay<OverlayItem> {
  private Context context;
  private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
 
  public PlacesItemizedOverlay(Context aContext, Drawable marker) {
    super(boundCenterBottom(marker));
    context = aContext;
  } 
 
    public void addOverlayItem(OverlayItem item) {
        items.add(item);
        populate();
    }
 
    @Override
    protected OverlayItem createItem(int i) {
        return (OverlayItem) items.get(i);
    }
 
    @Override
    public int size() {
        return items.size();
    }
 
    @Override
    protected boolean onTap(int index) {
      OverlayItem item = (OverlayItem)items.get(index);
      AlertDialog.Builder dialog = new AlertDialog.Builder(context);
      dialog.setTitle(item.getTitle());
      dialog.setMessage(item.getSnippet());
      dialog.show();
 
      return true;
    }
}

