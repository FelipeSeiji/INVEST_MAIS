const API_USERS = "http://localhost:8080/api/users";

function logout(){

    localStorage.removeItem("token");

    window.location.href = "index.html";
}

async function getUsers(){

    const token = localStorage.getItem("token");

    const response = await fetch(API_USERS,{
        method:"GET",
        headers:{
            "Authorization":"Bearer "+token
        }
    });

    if(response.status === 403 || response.status === 401){
        alert("Não autorizado");
        return;
    }

    const data = await response.json();

    document.getElementById("result").textContent = JSON.stringify(data,null,2);
}