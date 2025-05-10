const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import SettingsPage from "../../pages/SettingsPage";

beforeAll(() => {
  window.alert = jest.fn();
});

beforeEach(() => {
  mockNavigate.mockClear();
});

describe("Settings Page", () => {

  it("renders heading and theme button", () => {
    render(<SettingsPage />, { wrapper: MemoryRouter });
    expect(screen.getByRole("heading", { name: /settings/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /toggle theme/i })).toBeInTheDocument();
  });

  it("navigates to test page", async () => {
    render(<SettingsPage />, { wrapper: MemoryRouter });
    await userEvent.click(screen.getByRole("button", { name: /go to test page/i }));
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/"));
  });
});
