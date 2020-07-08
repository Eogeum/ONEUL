package com.oneul.extra;

public class ExtraTech {

////    텍스트 줄 수 제한
//    mEditText.addTextChangeListener(new TextWatcher()
//    {
//        String previousString = "";
//
//        @Override<p></p>
//        public void onTextChanged(CharSequence s, int start, int before, int count)
//        {
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after)
//        {
//            previousString= s.toString();
//        }
//
//        @Override
//        public void afterTextChanged(Editable s)
//        {
//            if (mEditText.getLineCount() >= 6)
//            {
//                mEditText.setText(previousString);
//                mEditText.setSelection(mEditText.length());
//            }
//        }
//    });
}

// fixme : 1일날 시작한 일과를 2일에 완료하면 1일로 이동하여 작성완료된 것이 보여야 될 것 같음
// fixme : 빈 화면에 넣을 일과를 추가하라는 이미지같은 가이드가 필요
// fixme : 화면에 날짜를 표시할 방법
// fixme : 1일날 시작한 일과를 2일에 완료하면 화면에 일과 시간을 어떻게 표시할 지
