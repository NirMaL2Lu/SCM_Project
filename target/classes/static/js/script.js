console.log("this is script file")


const toggleSideBar=()=>{
    if ($(".sidebar").is(":visible")) {
        //true 
        //sidebar band karna hai
         $(".sidebar").css("display","none");
         $(".content").css("margin-left","0%");
    } else {
        //false 
        //sidebar show karna hai
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
};