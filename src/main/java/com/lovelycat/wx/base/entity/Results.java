package com.lovelycat.wx.base.entity;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.lovelycat.wx.base.entity.Results.Result.*;


public final class Results implements Serializable {

    /**
     * 返回对象
     */
    private Result result;

    /**
     * 结果集
     */
    private Map<String, Object> body = new HashMap<>();

    private Results() {
    }

    private Results(Result success) {
        this.result = success;
    }

    /**
     * 构建返回成功结果
     */
    public static Results success() {
        return new Results(new Result(SUCCESS_CODE, SUCCESS_MSG));
    }

    /**
     * 构建失败结果
     */
    public static Results error() {
        return new Results(new Result(ERROR_CODE, ERROR_MSG));
    }

    public static Results of() {
        return new Results(new Result(SUCCESS_CODE, SUCCESS_MSG));
    }

    public static Result error(String msg) {
        return new Result(ERROR_CODE, msg);
    }

    /**
     * 返回结果
     */
    public Result result() {
        if (!body.isEmpty()) {
            result.setResult(body);
        }
        return result;
    }

    /**
     * 设置返回码
     *
     * @param code
     * @return
     */
    public Results code(int code) {
        result.setCode(code);
        return this;
    }

    /**
     * 设置返回信息
     */
    public Results msg(String msg) {
        result.setMsg(msg);
        return this;
    }

    /**
     * 设置返回信息
     */
    public Results body(Object res) {
        result.setResult(res);
        return this;
    }

    /**
     * 添加键值
     */
    public Results put(String key, Object value) {
        if (result.getResult() == null) {
            result.setResult(body);
        }
        body.put(key, value);
        return this;
    }

    /**
     * 处理成功
     */
    public static final Result SUCCESS = new Result(SUCCESS_CODE, SUCCESS_MSG);

    /**
     * 处理失败
     */
    public static final Result ERROR = new Result(ERROR_CODE, ERROR_MSG);

    /**
     * 请求受限
     */
    public static final Result LIMITATION_REQUEST = new Result(LIMITATION_REQUEST_CODE, LIMITATION_REQUEST_MSG);

    /**
     * 请求过快
     */
    public static final Result LOCK_ERROR = new Result(LOCK_ERROR_CODE, LOCK_ERROR_CODE_MSG);
    /**
     * 参数错误
     */
    public static final Result PARAMETER_INCORRECT = new Result(PARAMETER_INCORRECT_CODE, PARAMETER_INCORRECT_MSG);
    /**
     * token过期
     */
    public static final Result TOKEN_INCORRECT = new Result(TOKEN_INCORRECT_CODE, "token已过期!");

    public Map<String, Object> toMap() {
        return body;
    }

    /**
     * 返回对象
     */
    public static class Result implements Serializable {

        //处理成功
        public static final int SUCCESS_CODE = 200;
        public static final String SUCCESS_MSG = "处理成功";
        //处理失败
        public static final int ERROR_CODE = -1;
        public static final String ERROR_MSG = "处理失败";

        /**
         * 4000x
         */
        // 请求参数有误
        public final static int PARAMETER_INCORRECT_CODE = 40002;
        public static final String PARAMETER_INCORRECT_MSG = "请求参数有误";


        /**
         * 5000x
         */
        //请求受限
        public static final int LIMITATION_REQUEST_CODE = 50001;
        public static final String LIMITATION_REQUEST_MSG = "请求受限";

        public static final int LOCK_ERROR_CODE = 50002;
        public static final String LOCK_ERROR_CODE_MSG = "请求过快";
        /**
         * token过期
         */
        public final static int TOKEN_INCORRECT_CODE = 50000;

        protected Result() {
        }

        protected Result(int code, String msg, Object result) {
            this.code = code;
            this.msg = msg;
            this.result = result;
        }

        protected Result(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private int code;
        private String msg;
        private Object result;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }
    }


}

