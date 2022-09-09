$('#pills-transaction-tab').on('click', () => {
    $.get('/api/users/transactions', (response) => {
        console.log(response)
        let i = 1
        response.forEach(element => {
            let type = ''
            let method = ''
            let eType = element.type
            let eMethod = element.method
            let status = element.status == 0 ? "Pending" : (element.status == 1 ? "Success" : "Fail")
            switch(eType) {
                case 0:
                    type = 'Deposit'
                    method = eMethod == 0 ? 'VNPAY' : (eMethod == 1 ? 'MOMO' : 'BTC')
                    break
                case 1: 
                    type = 'Withdraw'
                    method = eMethod == 1 ? 'Bank' : 'BTC'
                    break
                case 2:
                    type = 'Transfer'
                    method = 'None'
                    break
            }

            
            let tr = `<tr>
                        <td>${i}</td>
                        <td>${element.code}</td>
                        <td>${type}</td>
                        <td>${method}</td>
                        <td>${parseFloat(element.amount).toLocaleString('en-US', {style: 'currency', currency: 'USD', minimumFractionDigits:6})}</td>
                        <td>${element.created_at}</td>
                        <td>${status}</td>
                        <td>${element.bcaddress != null ? element.bcaddress  : "None"}</td>
                        <td>${element.receiver != null ? element.receiver  : "None"}</td>
                    </tr>`
            $('#table-transaction-history').append(tr)
            i++
        })
    })
})