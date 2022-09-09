// Set our main variables
let scene,
renderer,
camera,
mixer, // THREE.js animations mixer
clock = new THREE.Clock() // Used for anims, which run to a clock instead of frame rate 

init()


function loadModel(scene, path) {
    var selectedObject = scene.getObjectByName("object_model")
    //selectedObject.material.transparent = true
    scene.remove(selectedObject)

    const loader = new THREE.FBXLoader()
    loader.load(path, function (object) {
        object.scale.set(0.075, 0.075, 0.075)
        moe = object
        moe.position.y = -11
        mixer = new THREE.AnimationMixer(object)
        action = mixer.clipAction(object.animations[0])
        mixer.stopAllAction()
        action.play()
        object.name = "object_model"
        scene.add(object)
    })
}

function init() {
    const canvas = document.querySelector('#c')
    scene = new THREE.Scene()

    renderer = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true })
    renderer.setClearColor(0xffffff, 0)
    renderer.shadowMap.enabled = true;
    renderer.setPixelRatio(window.devicePixelRatio)
    $('#wp-character').append(renderer.domElement)

    // Add a camera
    camera = new THREE.PerspectiveCamera(
        50,
        $('#wp-character').width() / $('#wp-character').height(),
        //window.innerWidth / window.innerHeight,
        0.1,
        1000);

    camera.position.z = 30;
    camera.position.x = 0;
    camera.position.y = -3;



    // Add lights
    let hemiLight = new THREE.HemisphereLight(0xffffff, 0xffffff, 0.61);
    hemiLight.position.set(0, 50, 0);
    // Add hemisphere light to scene
    scene.add(hemiLight);

    let d = 8.25;
    let dirLight = new THREE.DirectionalLight(0xffffff, 0.54);
    dirLight.position.set(-8, 12, 8);
    dirLight.castShadow = false;
    dirLight.shadow.mapSize = new THREE.Vector2(1024, 1024);
    dirLight.shadow.camera.near = 0.1;
    dirLight.shadow.camera.far = 1500;
    dirLight.shadow.camera.left = d * -1;
    dirLight.shadow.camera.right = d;
    dirLight.shadow.camera.top = d;
    dirLight.shadow.camera.bottom = d * -1;
    scene.add(dirLight);



    loadModel(scene, "/models/dwarf_idle.fbx")
}

function update() {
    if (mixer) {
        mixer.update(clock.getDelta());
    }

    if (resizeRendererToDisplaySize(renderer)) {
        const canvas = renderer.domElement;
        camera.aspect = canvas.clientWidth / canvas.clientHeight;
        camera.updateProjectionMatrix();
    }

    renderer.render(scene, camera);
    requestAnimationFrame(update);
}

update();

function resizeRendererToDisplaySize(renderer) {
    const canvas = renderer.domElement;
    let width = $('#wp-character').width()// window.innerWidth;
    let height = $('#wp-character').height()// window.innerHeight;

    let canvasPixelWidth = canvas.width / window.devicePixelRatio;
    let canvasPixelHeight = canvas.height / window.devicePixelRatio;

    const needResize =
        canvasPixelWidth !== width || canvasPixelHeight !== height;
    if (needResize) {
        renderer.setSize(width / 2, height / 2, false);

    }
    return needResize;
}

let spinning = false
const spinningTime = 10


let slices1 //= ["X0,5", "X0", "NEXT", "X0,5", "X0", "X1,5", "X1,0", "NEXT"]
let slices2 //= ["X1,5", "NEXT", "X2", "X0", "X1,5", "X5", "X2", "X0"]
let slices3 //= ["X3", "X35", "X2", "X3", "X2", "X0", "X10", "X2", "X3", "X0", "X2", "X3", "X5", "X0", "X3", "X2"]

let colors1 //= ["#5B18B4", "#432B4F", "#9AD537", "#5B18B4", "#432B4F", "#FF5A0A", "#C01FCF", "#432B4F"]
let textColors1 //= ["#D6C5EC", "#5A4565", "#E6F4CD", "#D6C5EC", "#5A4565", "#FFBA00", "#EFC9F3", "#5A4565"]

let colors2 //= ["#5B18B4", "#9AD537", "#C01FCF", "#432B4F", "#5B18B4", "#9AD537", "#C01FCF", "#432B4F"]
let textColors2 //= ["#D6C5EC", "#E6F4CD", "#EFC7F3", "#665370", "#D6C5EC", "#E6F4CD", "#EFC7F3", "#675471"]

let colors3 //= ["#B91DC7", "#FD0E1D", "#5B18B2", "#C01FCF", "#5B18B4", "#432B4F", "#FF5A0A", "#5B18B4", "#C01FCF", "#422A4D", "#5B18B4", "#C01FCF", "#FF5A0A", "#432B4F", "#C01FCF", "#5B18B4"]
let textColors3 //= ["#EDC5F1", "#FCA608", "#EDC5F1", "#EDC5F1", "#D4C3EA", "#65516E", "#FDB902", "#D4C3EA", "#EDC5F1", "#64516D", "#D4C3EA", "#EDC5F1", "#FDB902", "#65516E", "#EDC5F1", "#D4C3EA"]


function drawWheel(ctnId, cvId, fillColor, isGradient, isShadownRound, isStroke) {
    const smWheelSize = {
        x: $("#" + ctnId).width(),
        y: $("#" + ctnId).height()
    }
    var c = document.getElementById(cvId);
    var ctx = c.getContext("2d");
    ctx.beginPath();

    ctx.arc(smWheelSize.x / 2, smWheelSize.y / 2, smWheelSize.x / 2 - 10, 0, 2 * Math.PI);
    if (isGradient) {
        const grd = ctx.createRadialGradient(smWheelSize.x / 2, smWheelSize.y / 2, 3, smWheelSize.x / 2, smWheelSize.y / 2, smWheelSize.x); //x,y,r--> radius of white,x1,y1,r1--> radius of red
        grd.addColorStop(0, "red");
        grd.addColorStop(1, "white");
        ctx.fillStyle = grd
    } else {
        ctx.fillStyle = fillColor
    }



    ctx.restore();

    if (isShadownRound) {

        ctx.shadowColor = "#000000";
        ctx.strokeStyle = "rgba(0,0,0,1)";
        ctx.shadowBlur = 10;
        ctx.shadowOffsetX = 0;
        ctx.shadowOffsetY = 0;
        ctx.save();
    }
    if (isStroke) {
        ctx.strokeStyle = "rgba(255,255,255,0.2)"
        ctx.lineWidth = 15
        ctx.stroke()
    }


    ctx.fill()
    ctx.restore()
}

function deg2rad(deg) {
    return deg * Math.PI / 180;
}

function drawSlice(ctnId, cvId, color, label, deg, sliceDeg) {
    let canvasWidth = $("#" + cvId).width()

    let width = parseInt(canvasWidth)
    let r = width / 2 - 5
    let center = width / 2;

    const c = document.getElementById(cvId);
    let ctx = c.getContext("2d");
    ctx.save();
    ctx.beginPath();

    ctx.moveTo(center, center);
    ctx.arc(center, center, r, deg2rad(deg), deg2rad(deg + sliceDeg))
    ctx.lineTo(center, center)

    ctx.fillStyle = color;
    ctx.shadowColor = "transparent"
    ctx.strokeStyle = "#fff"
    ctx.lineWidth = 0.5

    ctx.shadowBlur = 10
    ctx.stroke()
    ctx.closePath()

    ctx.fill();
//ctx.restore()
}

function drawWheelSlice(ctnId, cvId, slices, colors, textColors) {
    let sliceDeg = 360 / slices.length
    let deg = -(sliceDeg / 2)
    let plus = cvId != "large-wheel" ? 50 : 25

    for (let i = 0; i < slices.length; i++) {

        drawSlice(ctnId, cvId, colors[i], slices[i], deg, sliceDeg)
        drawText(ctnId, cvId, parseFloat(deg - sliceDeg / 2 + plus), slices[i], textColors[i])

        deg += sliceDeg
    }
}

function drawText(ctnId, cvId, deg, text, color) {
    let canvasWidth = $("#" + cvId).width()
    let fontSize = 200
    let width = parseInt(canvasWidth)
    const wheelTextSize = parseInt(canvasWidth / 60) * parseInt(fontSize) / 70
    let center = width / 2
    const c = document.getElementById(cvId)
    let ctx = c.getContext("2d")

    ctx.translate(center, center);
    ctx.rotate(deg2rad(deg));
    ctx.textAlign = "right";
    ctx.fillStyle = color;
    ctx.font = '200 ' + wheelTextSize + 'px Play';
    let reText = text.split('\/n'), text1 = '', text2 = '';
    if (reText.length > 1) {
        text1 = reText[0];
        text2 = reText.splice(1, reText.length - 1);
        text2 = text2.join('');
    } else {
        text1 = text
        text2 = ""
    }
    let plus = cvId != "large-wheel" ? (cvId == "md-wheel" ? 8.4 : 8.7) : 8.3
    ctx.fillText(text1.trim(), 8 * center / plus, 0);
    ctx.restore()
}

function drawDot(ctnId, cvId, slices, on) {
    let canvasWidth = $("#" + cvId).width()
    let width = parseInt(canvasWidth)
    let center = width / 2
    let sliceDeg = 360 / slices.length
    const c = document.getElementById(cvId)
    let ctx = c.getContext("2d")

    let center1 = center - 2 * 2;
    let x_val, y_val, deg;
    deg = sliceDeg / 2;
    for (let i = 0; i < slices.length; i++) {
        ctx.beginPath();
        if (on && i % 2 == 0) {
            ctx.fillStyle = "#F8D200"
        } else if (!on && i % 2 != 0) {
            ctx.fillStyle = "#F8D200"
        } else {
            ctx.fillStyle = "#fff"
        }

        x_val = center + center1 * Math.cos(deg * Math.PI / 180);
        y_val = center - center1 * Math.sin(deg * Math.PI / 180);
        ctx.shadowColor = "transparent"
        ctx.arc(x_val, y_val, 6, 0, Math.PI * 2, false);
        ctx.fill();
        deg += sliceDeg;
    }


    setInterval(function () {
        ctx.restore()
        on = !on
        deg = sliceDeg / 2;
        for (let i = 0; i < slices.length; i++) {
            ctx.beginPath();
            if (on && i % 2 == 0) {
                ctx.fillStyle = "#F8D200"
            } else if (!on && i % 2 != 0) {
                ctx.fillStyle = "#F8D200"
            } else {
                ctx.fillStyle = "#fff"
            }

            x_val = center + center1 * Math.cos(deg * Math.PI / 180);
            y_val = center - center1 * Math.sin(deg * Math.PI / 180);
            ctx.shadowColor = "transparent"
            ctx.arc(x_val, y_val, 6, 0, Math.PI * 2, false);
            ctx.fill();
            deg += sliceDeg;
        }
    }, 1000);


}

async function spinsWheel(ctnId, cvId, stopPosition, wheelSpeed, spinningTime, resultStatus, resultMsg, slices) {
    return await new Promise(resolve => {
        let sliceDeg = 360 / slices.length
        let canvas = $('#' + cvId)
        let stopDeg = 360 - sliceDeg * stopPosition
        let wheelStop = wheelSpeed * 360 * spinningTime + stopDeg

        let css = '-moz-transform: rotate(' + wheelStop + 'deg);-webkit-transform: rotate(' + wheelStop + 'deg);-o-transform: rotate(' + wheelStop + 'deg);-ms-transform: rotate(' + wheelStop + 'deg);transform: rotate(' + wheelStop + 'deg);'
        css += '-webkit-transition: transform ' + spinningTime + 's ease;-moz-transition: transform ' + spinningTime + 's ease;-ms-transition: transform ' + spinningTime + 's ease;-o-transition: transform ' + spinningTime + 's ease;transition: transform ' + spinningTime + 's ease;'


        let cssRotateBg = '-moz-transform: rotate(-' + wheelStop + 'deg);-webkit-transform: rotate(-' + wheelStop + 'deg);-o-transform: rotate(-' + wheelStop + 'deg);-ms-transform: rotate(-' + wheelStop + 'deg);transform: rotate(-' + wheelStop + 'deg);'
        cssRotateBg += '-webkit-transition: transform ' + spinningTime + 's ease;-moz-transition: transform ' + spinningTime + 's ease;-ms-transition: transform ' + spinningTime + 's ease;-o-transition: transform ' + spinningTime + 's ease;transition: transform ' + spinningTime + 's ease;'

        canvas.attr('style', css)
        spinning = true
        $('#rotate-bg img').attr('style', cssRotateBg)
        setTimeout(function () {
            switch (resultStatus) {
                case 0:
                    console.log(resultStatus)
                    break
                case 1:
                    console.log(resultStatus)
                    break
                case 2:
                    console.log(resultStatus)
                    break
            }
            console.log(resultMsg)
            css = 'transform: rotate(' + stopDeg + 'deg);'
            canvas.attr('style', css);

            cssRotateBg = 'transform: rotate(-' + stopDeg + 'deg);'
            $('#rotate-bg img').attr('style', cssRotateBg)
            spinning = false;
            resolve()
        }, parseInt(spinningTime * 1000))
    })

}



// drawWheel('rotate-wheel', 'cv-rotate-wheel', 'orange', true, true, false)

drawWheel('cv-sm', 'sm-wheel', '#fff', false, true, true)

drawWheel('cv-md', 'md-wheel', '#07f6ff', false, true, true)

drawWheel('cv-large', 'large-wheel', '#34ff07', false, false, true)

$.get('/api/getSliceArrays', (response) => {
    console.log(response)
    //content
    slices1 = response.slices1
    slices2 = response.slices2
    slices3 = response.slices3
    //background color
    colors1 = response.colors1
    colors2 = response.colors2
    colors3 = response.colors3
    //text color
    textColors1 = response.textColors1
    textColors2 = response.textColors2
    textColors3 = response.textColors3
    drawWheelSlice('cv-sm', 'sm-wheel', slices1, colors1, textColors1)
    drawWheelSlice('cv-md', 'md-wheel', slices2, colors2, textColors2)
    drawWheelSlice('large-md', 'large-wheel', slices3, colors3, textColors3)
})

//drawDot('cv-sm', 'sm-wheel', slices1, true)

//drawDot('cv-md', 'md-wheel', slices2, true)
//drawDot('cv-large', 'large-wheel', slices3, true)

async function playAnimation(miliSeconds, target) {
    return new Promise(resolve => {
        window.setTimeout(function () {
            $(target).addClass('animate__animated animate__pulse')
            resolve()
        }, miliSeconds);
    })
}

let isBet = false

$(document).on('click', '#rotate-button', async function () {
    if (spinning) return
    if(!isBet){
        $('#bet-modal').modal('show')
        return
    }
    $('#rotate-button').addClass('d-none')
    $('#ov-cv-large').removeClass('d-none')
    $('#ov-cv-md').removeClass('d-none')

    loadModel(scene, "/models/dancing.fbx")

    let rotateButton = $('#rotate-wheel')
    let arrow = $('#arrow')
    $(rotateButton).removeClass('animate__animated animate__pulse')
    $(arrow).removeClass('d-none')


    $.post('/api/wheels/result', {"betAmount": $('input[name="wheel_bet_amount"]').val()}, async (response) => {
        // var response = await rsp
        if(response.code == 200){
            const posSM = response.sm
            const posMD = response.md
            const posLG = response.lg
            console.log(posSM)
            console.log(posMD)
            console.log(posLG)
            console.log(response)
            await spinsWheel('cv-sm', 'sm-wheel', posSM, 10, 3, response.status, response.message, slices1)
            if (posSM == 'NEXT') {
                spinning = true
                $('#ov-cv-md').addClass('d-none')
                await spinsWheel('cv-md', 'md-wheel', posMD, 10, 3, response.status, response.message, slices2)

                if (posMD == 'NEXT') {// next - spin 3
                    spinning = true
                    $('#ov-cv-large').addClass('d-none')
                    await spinsWheel('cv-large', 'large-wheel', posLG, 10, 3, response.status, response.message, slices3)
                }
            }
            let histories = response.histories
            $('#wheel-game-history').empty()
            let i = 1
            histories.forEach(element => {
                let tr = `<tr>
                            <th scope="row">${i}</th>
                            <td>${element.results}</td>
                            <td>${element.status}</td>
                            <td>${element.created_at}</td>
                        </tr>`
                $('#wheel-game-history').append(tr)
                i++
            })
            $('#wheel-balance').text(response.balance)
            
            $('#ov-cv-large').addClass('d-none')
            $('#ov-cv-md').addClass('d-none')
            $('#rotate-button').removeClass('d-none')

            $(arrow).addClass('d-none')

            loadModel(scene, "/models/happy_idle.fbx")

            $('#cv-large').removeClass('animate__animated animate__pulse')
            $('#cv-md').removeClass('animate__animated animate__pulse')
            $('#cv-sm').removeClass('animate__animated animate__pulse')

            await playAnimation(50, '#cv-large')
            await playAnimation(500, '#cv-md')
            await playAnimation(1000, '#cv-sm')
            loadModel(scene, "/models/dwarf_idle.fbx")

            $(rotateButton).addClass('animate__animated animate__pulse')

            console.log("End of wheel")
        } else {
            console.log(response)
        }
    }).fail((data) => {
        console.log(data)
    })

})

//add
$('#btn-wheel-bet').on('click', () => {
    $('#wheel-bet-amount-err').text("")
    let bet = $('input[name="wheel_bet_amount"]').val()
    //if(bet == null || bet.trim() == "") return
    $.get('/api/user/wallet/check-balance', {"bet" : bet}, (response) => {
        console.log(response)
        if(response.code == 200){
            isBet = true
        } else { 
            isBet = false
            $('#wheel-bet-amount-err').text(response.amount)
        }
    }).fail((data) => {
        console.log(data)
        isBet = false
        $('#wheel-bet-amount-err').text(data.responseText.msg)
    })
})