import requests
import json

def restCall(url):
    resp = requests.get(url)
    if resp.status_code != 200:
        # This means something went wrong.
        # raise ApiError('GET /tasks/ {}'.format(server.status_code))
        return resp.status_code, ""
    return resp.status_code, resp.json()

def cbas():
    server = 'https://stage-external-api-account.quest.com/idp/healthcheck'
    db = 'https://stage-external-api-account.quest.com/idp/healthcheck/getdbdate'
    result = {}
    x,y = restCall(server)
    a,b = restCall(db)
    result["CBAS"] = {"server": [x,y], "db" : [a,b]}
    return result

def profile():
    server = 'https://stage-profile.quest.com/healthcheck'
    db = 'https://stage-profile.quest.com/healthcheck/getDbDate'
    result = {}
    x,y = restCall(server)
    a,b = restCall(db)
    result["PROFILE"] = {"server": [x,y["response"]], "db" : [a,b["response"]]}
    return result
    
def qib():
    wellKnown = 'https://id-qa.quest.com/auth/realms/quest/.well-known/openid-configuration'
    certs = 'https://id-qa.quest.com/auth/realms/quest/protocol/openid-connect/certs'
    result = {}
    x1,y1 = restCall(wellKnown)
    x2,y3 = restCall(certs)
    result["QIB"] = {"Well-known": x1, "certs": x2}
    return result

result = {}
result.update(cbas())
result.update(profile())
result.update(qib())
print(result)