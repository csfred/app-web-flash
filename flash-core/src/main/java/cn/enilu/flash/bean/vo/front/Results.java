package cn.enilu.flash.bean.vo.front;

/**
 * @author cs
 * @date 2020/07/28
 */
public class Results {

    public static final Integer SUCCESS = 20000;
    public static final Integer FAILURE = 9999;
    public static final Integer TOKEN_EXPIRE = 50014;

    public static Result success(Object data) {
        return new Result(Results.SUCCESS, "成功", data);
    }

    public static Result failure(String msg) {
        return new Result(Results.FAILURE, msg, null);
    }

    public static Result success() {
        return new Result(Results.SUCCESS, "成功", null);
    }

    public static Result expire() {
        return new Result(Results.TOKEN_EXPIRE, "token 过期", null);
    }
}
