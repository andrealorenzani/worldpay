import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Add offer")
    request {
        method 'DELETE'
        url 'offer/so1/pa$$w0rd'
        headers {
            header('Content-Type', 'application/json; charset=utf-8')
            header('Accept', 'application/json; charset=utf-8')
        }
    }

    response {
        status 400
        body(
                    code: "02",
                    message: "Wrong ID or MEMORABLEWORD"
        )
        headers {
            header('Content-Type', 'application/json;charset=utf-8')
        }
    }
}
