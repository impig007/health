//权限不足通用消息提示
var vueobje;
function showErrorMessage(res){
    if (res =='Error: Request failed with status code 403'){
        vueobje.$message.error("权限不足");
                return;
            }
            //未知错误
    vueobje.$message.error("未知错误");
            return;
}
