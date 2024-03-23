package com.fullcycle.catalogo.infrastructure.authentication;

public class KeycloakAuthenticationGateway  implements AuthenticationGateway {

  @Override
  public AuthenticationResult login(ClientCredentialsInput input) {
    return null;
  }

  @Override
  public AuthenticationResult refresh(RefreshTokenInput input) {
    return null;
  }
}
