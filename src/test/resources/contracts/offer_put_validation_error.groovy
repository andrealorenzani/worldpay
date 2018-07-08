import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Add offer")
    request {
        method 'PUT'
        url 'offer'
        body("""
            {}
        """)
        headers {
            header('Content-Type', 'application/json; charset=utf-8')
            header('Accept', 'application/json; charset=utf-8')
        }
    }

    response {
        status 400
        body(
                code: "03"
        )
        headers {
            header('Content-Type', 'application/json;charset=utf-8')
        }
    }
}
