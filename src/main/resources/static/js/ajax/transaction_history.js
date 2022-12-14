$('#pills-transaction-tab').on('click', () => {
    $.get('/users/transactions', (response) => {
        console.log(response)
        $('#table-transaction-history').empty();

        let i = 1
        response.forEach(element => {
            let type = ''
            let method = ''
            let currency = 'USD'
            let eType = element.type
            let eMethod = element.method
            let status = element.status == 0 ? "Pending" : (element.status == 1 ? "Success" : "Fail")
            switch(eType) {
                case 0:
                    type = 'Deposit'
                    method = eMethod == 0 ? 'VNPAY' : (eMethod == 1 ? 'MOMO' : 'BTC')
                    currency = element.status == 1 ? 'USD' : (element.status == 0 && element.method == 2 ? 'BTC' : 'VND')
                    break
                case 1: 
                    type = 'Withdraw'
                    method = eMethod == 1 ? 'Bank' : 'BTC'
                    currency = element.status == 1 ? 'USD' : (element.status == 0 && element.method == 1 ? 'VND' : 'BTC')
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
                        <td>${
                              element.status == 1 || element.type == 'Transfer'? 
                              parseFloat(element.received_amount).toLocaleString('en-US', {style: 'currency', currency: currency, minimumFractionDigits:6}):
                              parseFloat(element.amount).toLocaleString('en-US', {minimumFractionDigits:6})+currency
                            }</td>
                        <td>${new Intl.DateTimeFormat('en-US', {dateStyle: 'short', timeStyle: 'medium' }).format(new Date(element.created_at))}</td>
                        <td>${status}</td>
                        <td>${element.bcaddress != null ? element.bcaddress  : "None"}</td>
                        <td>${element.receiver != null ? element.receiver  : "None"}</td>
                    </tr>`
            $('#table-transaction-history').append(tr)
            i++
        })
    })
})