const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import SignupPage from "../../pages/SignupPage";


beforeAll(() => {
  window.alert = jest.fn();
});

beforeEach(() => {
  mockNavigate.mockClear();
});

describe("Signup Page", () => {

  it("renders all input fields and checkbox", () => {
    render(<SignupPage />, { wrapper: MemoryRouter });
    expect(screen.getByPlaceholderText(/first name/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/last name/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/username/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/^password$/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/confirm password/i)).toBeInTheDocument();
    expect(screen.getByRole("checkbox")).not.toBeChecked();
    expect(screen.getByRole("button", { name: /sign up/i })).toBeInTheDocument();
  });

  // it("prevents submission on password mismatch", async () => {
  //   render(<SignupPage />, { wrapper: MemoryRouter });
  //   await userEvent.type(screen.getByPlaceholderText(/^password$/i), "pass1");
  //   await userEvent.type(screen.getByPlaceholderText(/confirm password/i), "pass2");
  //   await userEvent.click(screen.getByRole("button", { name: /sign up/i }));
  //   expect(mockNavigate).not.toHaveBeenCalled();
  // });

  // it("submits correctly with matching passwords", async () => {
  //   render(<SignupPage />, { wrapper: MemoryRouter });
  //   await userEvent.type(screen.getByPlaceholderText(/^password$/i), "pass");
  //   await userEvent.type(screen.getByPlaceholderText(/confirm password/i), "pass");
  //   await userEvent.click(screen.getByRole("checkbox"));
  //   await userEvent.click(screen.getByRole("button", { name: /sign up/i }));
  //   await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/Login"));
  // });
});
