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

//    todo: 화면 전환 시 새로운 프래그먼트로 불러오지 말고 기존 프래그먼트로 불러오게
//todo footer 추가
//todo 캘린더 뷰 다이얼로그 화 및 일과 있는날 점 표시
//todo 사진 다운 스케일링 및 카메라, 갤러리 접속 등 사진 관련
// fixme : 화면에 날짜를 표시할 방법
// fixme : 1일날 시작한 일과를 2일에 완료하면 화면에 일과 시간을 어떻게 표시할 지