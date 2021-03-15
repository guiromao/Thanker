package co.thanker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.api.Distribution;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import co.thanker.R;
import co.thanker.data.DateShow;
import co.thanker.data.Thanks;
import co.thanker.data.User;
import co.thanker.utils.DataUtils;

public class ThanksDiaryAdapter extends ArrayAdapter<Thanks> {

    private static final String TAG = "ThanksDiaryAdapter";
    private static final String DB_REFERENCE = "users";
    private final String OWN_THANKS_DB = "own-thanks-db";

    private Context mContext;
    private FirebaseFirestore mFirestore;
    private User mUser;
    private List<Thanks> mListThanks;
    private List<String> mListIds;
    private List<DateShow> mListDates;
    private List<String> mListEditables;
    private List<IdText> mEditTextList;
    private CardView mCardGiver;
    private CardView mCardReceiver;

    public ThanksDiaryAdapter(@NonNull Context context, int resource, List<Thanks> list, List<String> listIds, User user) {
        super(context, resource, list);

        mContext = context;
        mFirestore = FirebaseFirestore.getInstance();
        mListThanks = list;
        mListIds = listIds;
        mListDates = new ArrayList<>();
        mListEditables = new ArrayList<>();
        mEditTextList = new ArrayList<>();
        mUser = user;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_own_thanks, parent, false);
        }

        final View thisView = listItemView;

        final Thanks thanks = getItem(position);

        final ImageView thanksImage = (ImageView) listItemView.findViewById(R.id.image_thanks_type);
        CardView cardLayout = (CardView) listItemView.findViewById(R.id.cardview_thanks_table);

        cardLayout.setBackground(mContext.getResources().getDrawable(R.drawable.cardview_thanks_shape));
        thanksImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thanks));

        TextView dateText = (TextView) listItemView.findViewById(R.id.thanks_item_date);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date(thanks.getDate()));
        Date day = new Date(thanks.getDate());
        DateShow currentDateShow;
        int visibility;

        sortDates();

        if (position == 0) {
            currentDateShow = new DateShow(day, true);
            mListDates.add(currentDateShow);
            visibility = View.VISIBLE;
        } else {
            Calendar one = Calendar.getInstance();
            one.setTime(new Date(mListThanks.get((int) position - 1).getDate()));
            if (isItSameCalendar(one, currentCalendar)) {
                visibility = View.GONE;
                Log.v(TAG, "Checking date visibility. Day " + day + ", and day " + new Date(mListThanks.get((int) position - 1).getDate()) + " are considered the same");
            } else {
                visibility = View.VISIBLE;
                Log.v(TAG, "Checking date visibility. Day " + day + ", and day " + new Date(mListThanks.get((int) position - 1).getDate()) + " are NOT considered the same");
            }
        }

        dateText.setVisibility(visibility);
        String dateString = DataUtils.getDateString(mContext, mListThanks.get((int) position).getDate());
        dateText.setText(dateString);
        Log.v(TAG, "New Date formula. Written date: " + day + ", in position: " + position + ", dateString: " + dateString);

        final LinearLayout linearThanks = listItemView.findViewById(R.id.linear_thanks);
        final TextView thanksTitleText = (TextView) listItemView.findViewById(R.id.text_thanks_title);
        final TextView thanksDescriptionText = (TextView) listItemView.findViewById(R.id.text_thanks_description);
        final CardView cardThanks = listItemView.findViewById(R.id.cardview_thanks_table);
        final LinearLayout linearEdit = thisView.findViewById(R.id.linear_edit);
        final ImageView imageInfo = thisView.findViewById(R.id.image_info);
        final Spinner catSpinner = thisView.findViewById(R.id.spinner_category);
        final EditText input = thisView.findViewById(R.id.input_thanks);
        final Button buttonThank = thisView.findViewById(R.id.button_thank);

        String description = thanks.getDescription();

        String thanksTitleString = (chooseTypeThanksGiver(thanks.getThanksType())).trim() + " " + mContext.getString(R.string.in_the_category_of, DataUtils.translateAndFormat(mContext, thanks.getPrimaryCategory()));

        thanksTitleText.setText(Html.fromHtml(thanksTitleString));
        thanksDescriptionText.setText(Html.fromHtml("\"" + description + "\"."));

        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)
                && now.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
                && now.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) {
            linearEdit.setVisibility(View.VISIBLE);
        } else {
            linearEdit.setVisibility(View.INVISIBLE);
        }

        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle(mContext.getString(R.string.what_is_edit));
                builder.setMessage(mContext.getString(R.string.this_is_edit));
                builder.setPositiveButton(mContext.getString(R.string.got_it),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        if (!DataUtils.doesListContainItem(mListEditables, mListIds.get(position))) {

            linearEdit.setVisibility(View.INVISIBLE);
            linearThanks.setVisibility(View.VISIBLE);

            if (now.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)
                    && now.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
                    && now.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) {

                linearEdit.setVisibility(View.VISIBLE);

                listItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!DataUtils.doesListContainItem(mListEditables, mListIds.get(position))) {
                            mListEditables.add(mListIds.get(position));
                            Log.v(TAG, "These are the editables\n" + printEditables());
                            imageInfo.setVisibility(View.GONE);
                            buttonThank.setVisibility(View.VISIBLE);
                            thanksDescriptionText.setVisibility(View.GONE);
                            thanksTitleText.setText(Html.fromHtml((chooseTypeThanksGiver(thanks.getThanksType())).trim() + " " + mContext.getString(R.string.in_the_category_of_no_cat)));
                            catSpinner.setVisibility(View.VISIBLE);
                            input.setVisibility(View.VISIBLE);
                            initializeCategorySpinner(catSpinner, thanks.getPrimaryCategory());
                            input.setText(thanks.getDescription());

                            input.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (s.toString().length() > 0) {
                                        buttonThank.setBackground(mContext.getResources().getDrawable(R.drawable.button_rounded_green));
                                        buttonThank.setEnabled(true);
                                        input.setSelection(s.toString().length());
                                        thanks.setDescription(s.toString());
                                    } else {
                                        buttonThank.setBackground(mContext.getResources().getDrawable(R.drawable.button_rounded_grey));
                                        buttonThank.setEnabled(false);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            buttonThank.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String thanksKey = mListIds.get(position);
                                    final String previousCategory = thanks.getPrimaryCategory();
                                    String description = input.getText().toString();
                                    String category = DataUtils.translateToEnglish(mContext, catSpinner.getSelectedItem().toString());
                                    String thanksTitleString = (chooseTypeThanksGiver(thanks.getThanksType())).trim() + " " + mContext.getString(R.string.in_the_category_of, DataUtils.translateAndFormat(mContext, category));
                                    thanksTitleText.setText(Html.fromHtml(thanksTitleString));
                                    thanksDescriptionText.setText(Html.fromHtml("\"" + description + "\"."));
                                    thanks.setDescription(description);
                                    thanks.setPrimaryCategory(category);
                                    mFirestore.collection(OWN_THANKS_DB).document(thanksKey).set(thanks);

                                    mUser.addValueOnCategory(-1, previousCategory);
                                    mUser.addValueOnCategory(1, category);
                                    mFirestore.collection(DB_REFERENCE).document(mUser.getUserId()).set(mUser);

                                    imageInfo.setVisibility(View.VISIBLE);
                                    buttonThank.setVisibility(View.GONE);
                                    thanksDescriptionText.setVisibility(View.VISIBLE);
                                    catSpinner.setVisibility(View.GONE);
                                    input.setVisibility(View.GONE);

                                    if (DataUtils.doesListContainItem(mListEditables, mListIds.get(position))) {
                                        removeEditable(mListIds.get(position));
                                    }
                                }
                            });
                        } /*else {
                            if (DataUtils.doesListContainItem(mListEditables, mListIds.get(position))) {
                                removeEditable(mListIds.get(position));
                            }

                            String thanksTitleString = (chooseTypeThanksGiver(thanks.getThanksType())).trim() + " " + mContext.getString(R.string.in_the_category_of, DataUtils.translateAndFormat(mContext, thanks.getPrimaryCategory()));
                            thanksTitleText.setText(Html.fromHtml(thanksTitleString));
                            thanksDescriptionText.setVisibility(View.VISIBLE);
                            catSpinner.setVisibility(View.GONE);
                            input.setVisibility(View.GONE);
                            imageInfo.setVisibility(View.VISIBLE);
                            buttonThank.setVisibility(View.GONE);
                        }*/
                    }
                });

            } else {
                linearEdit.setVisibility(View.INVISIBLE);
            }
        } else {
            thanksDescriptionText.setVisibility(View.GONE);
            thanksTitleText.setText(Html.fromHtml((chooseTypeThanksGiver(thanks.getThanksType())).trim() + " " + mContext.getString(R.string.in_the_category_of_no_cat)));
            catSpinner.setVisibility(View.VISIBLE);
            input.setVisibility(View.VISIBLE);
            imageInfo.setVisibility(View.GONE);
            buttonThank.setVisibility(View.VISIBLE);
            initializeCategorySpinner(catSpinner, thanks.getPrimaryCategory());
            input.setText(thanks.getDescription());
            input.setSelection(input.getText().toString().length());
            Log.v(TAG, "These are the editables. The Thanks Key was already added\n" + printEditables());
            initializeCategorySpinner(catSpinner, thanks.getPrimaryCategory());
            input.setText(thanks.getDescription());

            buttonThank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String thanksKey = mListIds.get(position);
                    final String previousCategory = thanks.getPrimaryCategory();
                    String description = input.getText().toString();
                    String category = DataUtils.translateToEnglish(mContext, catSpinner.getSelectedItem().toString());
                    String thanksTitleString = (chooseTypeThanksGiver(thanks.getThanksType())).trim() + " " + mContext.getString(R.string.in_the_category_of, DataUtils.translateAndFormat(mContext, category));
                    thanksTitleText.setText(Html.fromHtml(thanksTitleString));
                    thanksDescriptionText.setText(Html.fromHtml("\"" + description + "\"."));
                    thanks.setDescription(description);
                    thanks.setPrimaryCategory(category);
                    mFirestore.collection(OWN_THANKS_DB).document(thanksKey).set(thanks);

                    mUser.addValueOnCategory(-1, previousCategory);
                    mUser.addValueOnCategory(1, category);
                    mFirestore.collection(DB_REFERENCE).document(mUser.getUserId()).set(mUser);

                    thanksDescriptionText.setVisibility(View.VISIBLE);
                    catSpinner.setVisibility(View.GONE);
                    input.setVisibility(View.GONE);
                    imageInfo.setVisibility(View.VISIBLE);
                    buttonThank.setVisibility(View.GONE);

                    if (DataUtils.doesListContainItem(mListEditables, thanksKey)) {
                        removeEditable(thanksKey);
                    }
                }
            });
        }

        return listItemView;
    }

    public void removeEditable(String id) {
        for (int i = 0; i != mListEditables.size(); i++) {
            if (mListEditables.get(i).equalsIgnoreCase(id)) {
                Log.v(TAG, "These are the editables. Going to remove " + mListEditables.get(i));
                mListEditables.remove(i);
                i--;
            }
        }
    }

    public List<Boolean> makeEditablesList() {
        List<Boolean> result = new ArrayList<>();

        for (String id : mListIds) {
            result.add(false);
        }

        return result;
    }

    public void sortDates() {
        Collections.sort(mListDates, new Comparator<DateShow>() {
            @Override
            public int compare(DateShow o1, DateShow o2) {
                return Long.compare(o2.getDate().getTime(), o1.getDate().getTime());
            }
        });
    }

    public String printEditables() {
        String result = "";
        for (String item : mListEditables) {
            result += item + "\n";
        }
        return result;
    }

    public boolean isItSameCalendar(Calendar one, Calendar two) {
        return (one.get(Calendar.DAY_OF_MONTH) == two.get(Calendar.DAY_OF_MONTH)
                && one.get(Calendar.MONTH) == two.get(Calendar.MONTH)
                && one.get(Calendar.YEAR) == two.get(Calendar.YEAR));
    }

    public void initializeCategorySpinner(Spinner spinner, String chosenCategory) {
        List<String> categories = new ArrayList<>();

        for (User.Category cat : User.Category.values()) {
            String category = cat.toString();

            if (category.equals("")) {

            } else if (category.equalsIgnoreCase("Person")) {
                category = mContext.getString(R.string.person);
            } else if (category.equalsIgnoreCase("Brand")) {
                category = mContext.getString(R.string.brand);
            } else if (category.equalsIgnoreCase("Association")) {
                category = mContext.getString(R.string.association);
            } else {
                category = DataUtils.translateAndFormat(mContext, cat.toString());
            }

            if(!category.equals(""))
            {
                categories.add(category);
            }
        }

        ArrayAdapter<User.Category> categoriesPrimaryAdapter = new ArrayAdapter(mContext, R.layout.spinneritem, categories);

        spinner.setAdapter(categoriesPrimaryAdapter);
        spinner.setSelection(findCategory(chosenCategory));
    }

    public int findCategory(String category) {
        int result = 0;

        for (User.Category cat : User.Category.values()) {
            if (cat.toString().equalsIgnoreCase(category)) {
                return (result - 1);
            }
            result++;
        }

        return 0;
    }

    public String chooseTypeThanksGiver(String type) {
        String result = "";

        switch (type.toLowerCase()) {
            case "normal":
                result = mContext.getString(R.string.thanked_adapter_2);
                break;

            case "super":
                result = mContext.getString(R.string.super_thanked_adapter_2);
                break;

            case "mega":
                result = mContext.getString(R.string.mega_thanked_adapter_2);
                break;

            case "power":
                result = mContext.getString(R.string.power_thanked_adapter_2);
                break;

            case "ultra":
                result = mContext.getString(R.string.ultra_thanked_adapter_2);
                break;
        }

        return result;
    }

    public class IdBoolean {

        private String id;
        private boolean editable;

        public IdBoolean(String id, boolean editable) {
            this.id = id;
            this.editable = editable;
        }

        public String getId() {
            return id;
        }

        public boolean isEditable() {
            return editable;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }
    }

    public class IdText {

        private String id;
        private String text;

        public IdText(String userId, String t) {
            id = userId;
            text = t;
        }

        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setText(String t) {
            text = t;
        }
    }


}
