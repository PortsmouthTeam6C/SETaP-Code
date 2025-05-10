const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import Login from "../../pages/Login";

beforeAll(() => {
  window.alert = jest.fn();
});

beforeEach(() => {
  mockNavigate.mockClear();
});


describe("Login Page", () => {

  it("renders all login elements", () => {
    render(<Login />, { wrapper: MemoryRouter });
    expect(screen.getByPlaceholderText(/username/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /log in/i })).toBeInTheDocument();
    expect(screen.getByText(/sign up/i)).toBeInTheDocument();
    expect(screen.getByText(/forgot password/i)).toBeInTheDocument();
  });

  it("accepts username and password input", async () => {
    render(<Login />, { wrapper: MemoryRouter });
    await userEvent.type(screen.getByPlaceholderText(/username/i), "user");
    await userEvent.type(screen.getByPlaceholderText(/password/i), "pass");
    expect(screen.getByPlaceholderText(/username/i)).toHaveValue("user");
    expect(screen.getByPlaceholderText(/password/i)).toHaveValue("pass");
  });

  it("navigates on login and link clicks", async () => {
    render(<Login />, { wrapper: MemoryRouter });
    await userEvent.click(screen.getByRole("button", { name: /log in/i }));
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/Homepage"));
    await userEvent.click(screen.getByText(/sign up/i));
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/SignUp"));
    await userEvent.click(screen.getByText(/forgot password/i));
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/ForgotPasswordPage"));
  });
});
