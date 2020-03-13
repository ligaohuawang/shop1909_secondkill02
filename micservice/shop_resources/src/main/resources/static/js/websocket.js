//初始化WebSocket
var ws;
var count;
var heartTime = 20000;
var closeTime = 50000;
var reconnTime = 10000;

function initWebSocket(callBack) {
    if (callBack.heartTime) {
        heartTime = callBack.heartTime;
    }
    if (callBack.closeTime) {
        closeTime = callBack.closeTime;
    }
    if (callBack.reconnTime) {
        reconnTime = callBack.reconnTime;
    }
    if (window.WebSocket) {
        ws = new WebSocket(callBack.url);
        ws.onopen = function () {
            console.log("服务器连接成功！")
            heart();
            //定时关闭服务器，5秒钟
            closeConnection();
            //调用自定义的myopen方法
            if (callBack.myopen) {
                callBack.myopen();
            }
        }

        ws.onclose = function () {
            console.log("服务器连接断开")
            //5秒重连一次服务器
            setTimeout(function () {
                reconn(callBack);
            }, 5000)
            //关闭心跳

            if (heartTimeout) {
                clearTimeout(heartTimeout);
                heartTimeout = null;
            }
            if (callBack.myclose) {
                callBack.myclose();
            }
        }
        ws.onerror = function () {
            console.log("服务器连接异常")
            if (callBack.myerror) {
                callBack.myerror();
            }
        }
        ws.onmessage = function (msg) {
            console.log("接收到服务器的消息:" + msg.data)
            var msgObject = JSON.parse(msg.data);
            if (msgObject.type == 2) {
                count++;
                //每收到一次心跳就将定时器关闭一下
                clearTimeout(closeTimeOut);
                //重新开启定时器，计时重新开始
                closeConnection();
                if (callBack.mymessage) {
                    callBack.mymessage(msgObject);
                }
            } else {
                if (callBack.mymessage) {
                    callBack.mymessage(msgObject);
                }
            }
        }
    } else {
        alert("您的浏览器不支持，请换个浏览器")
    }
}

//重连服务器
function reconn(callBack) {
    console.log("开始重连服务器...")
    initWebSocket(callBack);
    if (heartTimeout) {
        clearTimeout(heartTimeout);
        heartTimeout = null;
    }
}

//心跳
var heartTimeout = null;

function heart() {
    //构造心跳消息
    var heartMsg = {"type": 2};
    //第二种方式var heartMsg=new Object();heartMsg.type=2;
    //第三种方式var heartMsg=new Msg(2);function Msg(type) {
    //this.type=type;
    // }
    //发送消息
    console.log("发送了一次心跳");
    sendObjectMsg(heartMsg);
    heartTimeout = setTimeout(function () {
        heart();
    }, heartTime)

}

//字符串的发送方法
function sendMsg(msg) {
    if (ws) {//如果ws不为空，就发送消息给服务器
        ws.send(msg);
    } else {
        console.log("发送失败")
    }
}

//定时关闭服务器，5秒钟
var closeTimeOut = null;

function closeConnection() {
    closeTimeOut = setTimeout(function () {
        if (ws) {
            ws.close();
        }
    }, closeTime)
}

//对象的发送方法，先转成字符串再发送
function sendObjectMsg(object) {
    var stringmsg = JSON.stringify(object);
    //调用字符串的发送方法
    sendMsg(stringmsg);
}
