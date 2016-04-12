package com.booboot.vndbandroid.adapter.vndetails;

/**
 * Created by od on 18/03/2016.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.booboot.vndbandroid.R;
import com.booboot.vndbandroid.activity.VNDetailsActivity;
import com.booboot.vndbandroid.activity.VNTypeFragment;
import com.booboot.vndbandroid.adapter.doublelist.DoubleListAdapter;
import com.booboot.vndbandroid.adapter.doublelist.DoubleListElement;
import com.booboot.vndbandroid.api.VNDBServer;
import com.booboot.vndbandroid.api.bean.Item;
import com.booboot.vndbandroid.db.DB;
import com.booboot.vndbandroid.util.Callback;
import com.booboot.vndbandroid.util.Lightbox;
import com.booboot.vndbandroid.util.Pixels;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedHashMap;
import java.util.List;

public class VNExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> titles;
    private LinkedHashMap<String, VNDetailsElement> vnDetailsElements;

    public VNExpandableListAdapter(Context context, List<String> titles, LinkedHashMap<String, VNDetailsElement> vnDetailsElements) {
        this.context = context;
        this.titles = titles;
        this.vnDetailsElements = vnDetailsElements;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return getElement(listPosition).getPrimaryData().get(expandedListPosition);
    }

    private VNDetailsElement getElement(int listPosition) {
        return vnDetailsElements.get(getGroup(listPosition));
    }

    private Object getRightChild(int listPosition, int expandedListPosition) {
        List<String> rightData = getElement(listPosition).getSecondaryData();
        if (rightData == null || rightData.size() <= expandedListPosition) return null;
        return rightData.get(expandedListPosition);
    }

    public int getChildLayout(int listPosition) {
        int type = vnDetailsElements.get(getGroup(listPosition)).getType();
        if (type == VNDetailsElement.TYPE_TEXT) return R.layout.list_item_text;
        if (type == VNDetailsElement.TYPE_IMAGES) return R.layout.list_item_images;
        if (type == VNDetailsElement.TYPE_CUSTOM) return R.layout.list_item_custom;
        if (type == VNDetailsElement.TYPE_SUBTITLE) return R.layout.list_item_subtitle;
        return -1;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final String primaryText = (String) getChild(listPosition, expandedListPosition);
        final String secondaryText = (String) getRightChild(listPosition, expandedListPosition);
        final int layout = getChildLayout(listPosition);
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(layout, null);

        switch (layout) {
            case R.layout.list_item_text:
                ImageView itemLeftImage = (ImageView) convertView.findViewById(R.id.itemLeftImage);
                TextView itemLeftText = (TextView) convertView.findViewById(R.id.itemLeftText);
                TextView itemRightText = (TextView) convertView.findViewById(R.id.itemRightText);
                ImageView itemRightImage = (ImageView) convertView.findViewById(R.id.itemRightImage);

                itemLeftText.setText(Html.fromHtml(primaryText));

                if (secondaryText == null) itemRightText.setVisibility(View.GONE);
                else {
                    if (secondaryText.contains("</a>"))
                        itemRightText.setMovementMethod(LinkMovementMethod.getInstance());
                    itemRightText.setText(Html.fromHtml(secondaryText));
                }

                int leftImage;
                if (getElement(listPosition).getPrimaryImages() != null && (leftImage = getElement(listPosition).getPrimaryImages().get(expandedListPosition)) > 0) {
                    itemLeftImage.setImageResource(leftImage);
                } else {
                    itemLeftImage.setVisibility(View.GONE);
                }

                int rightImage;
                if (getElement(listPosition).getSecondaryImages() != null && (rightImage = getElement(listPosition).getSecondaryImages().get(expandedListPosition)) > 0) {
                    itemRightImage.setImageResource(rightImage);
                } else {
                    itemRightImage.setVisibility(View.GONE);
                }
                break;

            case R.layout.list_item_images:
                final ImageButton expandedListImage = (ImageButton) convertView.findViewById(R.id.expandedListImage);
                ImageLoader.getInstance().displayImage(primaryText, expandedListImage);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Pixels.px(100, context)));
                Lightbox.set(context, expandedListImage, primaryText);
                break;

            case R.layout.list_item_subtitle:
                ImageView iconView = (ImageView) convertView.findViewById(R.id.iconView);
                TextView title = (TextView) convertView.findViewById(R.id.title);
                TextView subtitle = (TextView) convertView.findViewById(R.id.subtitle);

                if (getElement(listPosition).getUrlImages() == null)
                    iconView.setVisibility(View.GONE);
                else {
                    String url = getElement(listPosition).getUrlImages().get(expandedListPosition);
                    ImageLoader.getInstance().displayImage(url, iconView);
                    Lightbox.set(context, iconView, url);
                }
                title.setText(primaryText);
                subtitle.setText(secondaryText);

                switch ((String) getGroup(listPosition)) {
                    case VNDetailsActivity.TITLE_RELATIONS:
                        final int vnId = getElement(listPosition).getPrimaryImages().get(expandedListPosition);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                VNDBServer.get("vn", DB.VN_FLAGS, "(id = " + vnId + ")", null, context, new Callback() {
                                    @Override
                                    protected void config() {
                                        if (results.getItems().size() < 1) return;

                                        Intent intent = new Intent(context, VNDetailsActivity.class);
                                        intent.putExtra(VNTypeFragment.VN_ARG, results.getItems().get(0));
                                        context.startActivity(intent);
                                    }
                                }, Callback.errorCallback(context));
                            }
                        });
                        break;

                    case VNDetailsActivity.TITLE_CHARACTERS:
                        final Item character = ((VNDetailsActivity) context).getCharacters().get(expandedListPosition);
                        final LinkedHashMap<String, DoubleListElement> characterData = new LinkedHashMap<>();
                        characterData.put("Description", new DoubleListElement("Description", character.getDescription(), true));
                        characterData.put("Gender", new DoubleListElement("Gender", character.getGender(), false));
                        characterData.put("Blood type", new DoubleListElement("Blood type", character.getBloodt(), false));
                        characterData.put("Aliases", new DoubleListElement("Aliases", character.getAliases(), false));
                        characterData.put("Height", new DoubleListElement("Height", character.getHeight() + "cm", false));
                        characterData.put("Weight", new DoubleListElement("Weight", character.getWeight() + "kg", false));
                        characterData.put("Bust-Waist-Hips", new DoubleListElement("Bust-Waist-Hips", character.getBust() + "-" + character.getWaist() + "-" + character.getHip() + "cm", false));
                        characterData.put("Birthday", new DoubleListElement("Birthday", character.getBirthday().toString(), false));

                        final DoubleListElement[] elements = characterData.values().toArray(new DoubleListElement[characterData.keySet().size()]);

                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View view = layoutInflater.inflate(R.layout.character_dialog, null);
                                ListView listView = (ListView) view.findViewById(R.id.listView);

                                listView.setAdapter(new DoubleListAdapter(context, elements));

                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                dialogBuilder.setView(view);
                                dialogBuilder.setTitle(character.getName());
                                dialogBuilder.setCancelable(true);
                                final AlertDialog dialog = dialogBuilder.show();
                                view.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                        break;
                }
                break;
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return vnDetailsElements.get(getGroup(listPosition)).getPrimaryData().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return titles.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return titles.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        // [TODO] return Preferences.copyToClipboardOnLongClick
        return true;
    }
}