
function updateScreen(messages) {

    var json = $.parseJSON(messages);
    var status = json.status;
    var result = json.result;
    var version = json.version;
    var msg = json.message;

    if (status === 'error') {
        // status が error の場合 -1
        // どのバージョンに対するメッセージか判定
        if (version === 'se7') {
            $("#errMsg4se7").html(msg);
            return;
        } else if (version === 'se8') {
            $("#errMsg4se8").html(msg);
            return;
        }
    } else if (status === 'info') {
        if (version === 'se7') {
            $("#msg4se7").html(msg);
            return;
        } else if (version === 'se8') {
            $("#msg4se8").html(msg);
            return;
        }
    } else {
        if (result === -1) {
            return;
        }
        // status が成功(success)の場合
        // どのバージョンに対するメッセージか判定し出力
        if (version === 'se7') {
            var td1 = $("table.se7 tr:eq(" + 0 + ") td:eq(" + result + ")");
            $(td1).append('<BR/><IMG SRC="./resources/image/duke.png" width="30" / >');
        } else if (version === 'se8') {
            var td1 = $("table.se8 tr:eq(" + 0 + ") td:eq(" + result + ")");
            $(td1).append('<BR/><IMG SRC="./resources/image/duke.png" width="30" / >');
        }
    }
}     