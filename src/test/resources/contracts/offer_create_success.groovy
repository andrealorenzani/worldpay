import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Add offer")
    request {
        method 'POST'
        url 'offer'
        body("""
            {
              "title": "New Tesla Model 3",
              "description": "The new Tesla Model 3 at a special price only in our shop",
              "vendor": "Andrea Lorenzani Spa.",
              "price": 100000,
              "currency": "EUR",
              "duration": 86400,
              "memorableWord": "pa\$\$w0rd"
            }
        """)
        headers {
            header('Content-Type', 'application/json; charset=utf-8')
            header('Accept', 'application/json; charset=utf-8')
        }
    }

    response {
        status 200
        body(
                id: $(producer(regex(uuid()))),
                title: 'New Tesla Model 3',
                description: 'The new Tesla Model 3 at a special price only in our shop',
                vendor: 'Andrea Lorenzani Spa.',
                price: 100000,
                currency: 'EUR',
                expiration: $(producer(regex("(3[01]|0[1-9]|[12][0-9])-(1[0-2]|0[1-9])-([0-9]{4}) (2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])")))

        )
        headers {
            header('Content-Type', 'application/json;charset=utf-8')
        }
    }
}
