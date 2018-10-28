package set.work.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import set.work.thread.BaseRequestRunnable;
import set.work.utils.GsonUtil;

/**
 * Created by Hugh on 2017/9/4.
 * 测试
 */

public class JsonResultBean<T> extends BaseResultBean<T> {
    public JsonResultBean(RequestListBean bean, String resultString, BaseRequestRunnable runnable) {
        super(bean, resultString, runnable);
    }

    @Override
    public void parseResult() {
        if (type == TYPE_STR) {//文件类型
            return;
        }
        //TODO 类型被擦除了
        Gson gson = new Gson();
        result = gson.fromJson(resultString, new TypeToken<T>() {}.getType());
    }
}
