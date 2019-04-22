<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>首页</title>
    <script src="js/jquery.min.js"></script>
    <style>

    </style>
</head>
<body>
adddddddddddddddddddddddd
</body>
</body>
<script>
    function longPolling2(){
        $.get('${pageContext.request.contextPath }/callable', function(data){
            console.log(data);
            $('#n2').html(data);
            longPolling2();
        });
    }
    longPolling2();
</script>
</html>