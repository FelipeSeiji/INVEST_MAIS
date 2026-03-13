const LOGIN_API = "/api/auth/login";
const REGISTER_API = "/api/auth/register";

/* LOGIN */

document.getElementById("loginForm")
.addEventListener("submit", async function(e){

    e.preventDefault();

    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const response = await fetch(LOGIN_API,{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify({
            login: email,
            password: password
        })
    });

    if(response.ok){

        const data = await response.json();

        localStorage.setItem("token", data.token);

        alert("Login realizado");

    }else{
        alert("Login inválido");
    }

});


/* REGISTER */

document.getElementById("registerForm")
.addEventListener("submit", async function(e){

    e.preventDefault();

    const name = document.getElementById("name").value;
    const email = document.getElementById("registerEmail").value;
    const password = document.getElementById("registerPassword").value;

    const response = await fetch(REGISTER_API,{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify({
            name:name,
            email:email,
            password:password,
            role:"USER"
        })
    });

    if(response.ok){
        alert("Conta criada com sucesso");
    }else{
        alert("Erro ao criar conta");
    }

});