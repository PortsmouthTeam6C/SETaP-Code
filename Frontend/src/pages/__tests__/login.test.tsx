import { TextEncoder, TextDecoder } from "util";
;(global as any).TextEncoder = TextEncoder;
;(global as any).TextDecoder = TextDecoder;

//import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Login from "../login";
import { UserContext } from "../../context/UserContext";
import { login as loginAPI, type LoginResponse } from "../../api/login";

jest.mock("../../api/login");
const mockLogin = jest.mocked(loginAPI);

// mock useNavigate
const navigateMock = jest.fn();
jest.mock("react-router-dom", () => {
  const real = jest.requireActual("react-router-dom");
  return {
    ...real,
    useNavigate: () => navigateMock,
  };
});

const mockUpdateUser = jest.fn();

const mockUserContext = {
  isLoggedIn: () => false,
  token: undefined,
  expiry: undefined,
  updateUser: mockUpdateUser,
  tryLogin: jest.fn(),
  logOut: jest.fn(),
  username: undefined,
  email: undefined,
  profilePicture: undefined,
  universityId: undefined,
  universityLogo: undefined,
  universityTheming: undefined,
};

describe("Login Page", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("renders centered card layout with logo, inputs and link", () => {
    render(
      <MemoryRouter>
        <UserContext.Provider value={mockUserContext}>
          <Login />
        </UserContext.Provider>
      </MemoryRouter>
    );

    // Logo
    expect(screen.getByAltText("Logo")).toBeInTheDocument();
    // Inputs
    expect(screen.getByPlaceholderText(/email/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/password/i)).toBeInTheDocument();
    // Button
    expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
    // Signup link
    const signupLink = screen.getByRole("link", {
      name: /don't have an account\? sign up/i,
    });
    expect(signupLink).toHaveAttribute("href", "/signup");
  });

  it("prevents submission when fields are empty", () => {
    render(
      <MemoryRouter>
        <UserContext.Provider value={mockUserContext}>
          <Login />
        </UserContext.Provider>
      </MemoryRouter>
    );

    fireEvent.click(screen.getByRole("button", { name: /login/i }));

    expect(mockLogin).not.toHaveBeenCalled();
    expect(mockUpdateUser).not.toHaveBeenCalled();
    expect(navigateMock).not.toHaveBeenCalled();
  });

  it("calls login API, then updateUser and navigates on success", async () => {
    // arrange
    const fakeResponse: LoginResponse = {
      token: "t0k3n",
      expiry: new Date("2099-01-01"),
      username: "alice",
      email: "alice@example.com",
      profilePicture: "",
      universityId: 99,
      universityPicture: "",
      theming: JSON.stringify({ primarycolor: "#00f" }),
    };
    mockLogin.mockResolvedValueOnce(fakeResponse);
    mockUpdateUser.mockResolvedValueOnce(undefined);

    render(
      <MemoryRouter>
        <UserContext.Provider value={mockUserContext}>
          <Login />
        </UserContext.Provider>
      </MemoryRouter>
    );

    // fill form
    fireEvent.change(screen.getByPlaceholderText(/email/i), {
      target: { value: "alice@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/password/i), {
      target: { value: "secretpw" },
    });

    // submit
    fireEvent.click(screen.getByRole("button", { name: /login/i }));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith("alice@example.com", "secretpw");
      expect(mockUpdateUser).toHaveBeenCalledWith(fakeResponse);
      expect(navigateMock).toHaveBeenCalledWith("/");
    });
  });
});
