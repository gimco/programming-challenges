(function() {
    {
        const t = d.body,
            n = 6,
            e = 6,
            o = 1,
            s = [
                ["V", "P", "R", "H", "S", "K"],
                ["M", "L", "C", "M", "F", "N"],
                ["F", "W", "B", "N", "H", "M"],
                ["S", "Y", "I", "G", "A", "Y"],
                ["V", "X", "U", "X", "N", "Y"],
                ["O", "P", "T", "G", "E", "G"]
            ],
            c = ["UIBC", "FHAN", "MBYV", "HANE", "MFSV"];
        let r = 300;
        w.onclose = (() => setTimeout(() => l.reload(), 1e3));
        let a = "";
        for (let t = 0; t < e; t++) {
            a += "<tr>";
            for (let e = 0; e < n; e++) a += `<td id="${e}-${t}">${s[t][e]}</td>`;
            a += "</tr>"
        }
        const i = `<div id="container">` + `<table style="width:${40*n}px; height:${40*e}px"><tbody>${a}</tbody></table>` + `<div id="words">` + `<h1>Level ${o}</h1>` + c.map(t => `<div id="word-${t}">${t}</div>`).join("") + `<div id="time-left">` + `Time left: <span id="time${o}">${r}</span>` + `</div>` + `<div>` + `</div>`;
        let f = null;
        const u = t => {
            const n = $(t).getBoundingClientRect();
            const e = n.left + (n.right - n.left >> 1);
            const o = n.top + (n.bottom - n.top >> 1);
            return [e, o]
        };
        d.selectWord = ((n, e, o) => {
            const [s, c] = n.split("-").map(Number);
            const [r, a] = e.split("-").map(Number);
            const i = Math.abs(s - r) == Math.abs(c - a);
            const [l, d] = u(n);
            const [f, m] = u(e);
            const b = Math.abs(l - f);
            const h = Math.abs(d - m);
            const M = $(`word-${o}`);
            if (!M) return;
            M.className = "word-done";
            let L = 0;
            let g = 0;
            i ? (L = s < r ? c < a ? 45 : -45 : c < a ? 135 : 225, g = 0 | Math.sqrt(b * b + h * h)) : (L = s === r ? c < a ? 90 : -90 : s < r ? 0 : 180, g = 0 | Math.max(b, h));
            t.innerHTML += `<div style="position:absolute; top:${d-16-2}px; left:${l-16-2}px; pointer-events:none; ; transform:rotate(${L}deg); transform-origin:18px 18px">` + `<svg width="${g+32+4}" height="36" xmlns="http://www.w3.org/2000/svg">` + `<rect stroke="#55f" stroke-width="3" fill-opacity="0.2" fill="#55f" x="2" y="2" width="${g+32}" rx="16" ry="16" height="32"/>` + `</svg>` + `</div>`
        });
        const m = t => {
                let n, e, o, s = 0;
                const c = t + "-saltbae";
                if (!c.length) return s;
                for (n = 0, o = c.length; n < o; n++) e = c.charCodeAt(n), s = (s << 5) - s + e, s |= 0;
                return Math.abs(s)
            },
            b = t => {
                if (!t.screenX || !t.screenY || !t.isTrusted) return;
                const n = t.target;
                if ("TD" === n.nodeName)
                    if (f) {
                        $(f).classList.remove("cell-selected");
                        const t = n.id,
                            e = `${f}-${t}`;
                        w.send(btoa(`${e}-${m(e)}`)), f = ""
                    } else f = n.id, n.classList.add("cell-selected")
            };
        t.innerHTML = i, t.addEventListener("click", b);
        let h = setInterval(() => {
            const t = $(`time${o}`);
            t ? (r--, r < 11 && (t.className = "big-time"), t.innerHTML = Math.max(0, r)) : clearInterval(h)
        }, 1e3);
        window[String.fromCharCode(36)] = d.getElementById.bind(d)
    }
})