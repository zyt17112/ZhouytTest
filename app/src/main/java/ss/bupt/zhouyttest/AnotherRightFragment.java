package ss.bupt.zhouyttest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by new on 2016/8/3.
 */
public class AnotherRightFragment extends Fragment{

    @Override
    public View onCreateView
            (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.another_right_fragment, container, false);
        return view;
    }
}
