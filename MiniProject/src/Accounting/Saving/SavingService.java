package Accounting.Saving;
import java.util.ArrayList;
import java.util.Scanner;

import Accounting.Account1;
import MiniProject.Members.Members;
import MiniProject.Members.MembersService;

public class SavingService {
	private SavingDao dao;
	public static String loginId;

	public SavingService() {
		dao = new SavingDao();
	}
	
	//계좌 추가
		public void addAccount(Scanner sc) {
			System.out.println("===계좌추가===");
			int exp = 0;
			double per;
			System.out.print("입금 금액을 입력하세요:");
			int balance = sc.nextInt();
			do{
				System.out.print("가입 개월을 선택하세요.(3개월 0.2%, 6개월 0.6%, 12개월 0.9%): ");
				exp = sc.nextInt();
			}
			while(!(exp==3||exp==6||exp==12));
			if(exp==3) {
				per = 0.2;
			}else if(exp==6){
				per = 0.6;
			}else {
				per = 0.9;
			};
			dao.insert(new Saving("",loginId,balance,null,null,per,0) , exp);
			System.out.println("===개설이 완료되었습니다===");
		}
		//계좌 번호 조회
		public void getByNum(Scanner sc) {
			System.out.println("===계좌번호로 검색===");
			System.out.println("계좌번호:");
			String account_num = sc.next();
			//계좌의 주인일 때도 조회.
			Saving s = dao.selectByNum(account_num);
			if(s.equals(null)|| !s.getId().equals(loginId)) {
				System.out.println("조회 할 수 없는 계좌입니다.");
				return;
			}
			//조회시 날짜가 음수이면 > exprie 확인후 0이면 이자 지급
			if(dao.getDate(account_num)<=0 && s.getExpiry()==0) {
				dao.updateExp(s);
			}
			System.out.println("1.입금하기 2.송금하기 3.계좌내역보기 4.계좌삭제하기");
			int x = sc.nextInt();
			switch (x) {
			case 1:
				deposit(sc,s,account_num);
				break;
			case 2:
				withdraw(sc,s, account_num);
				break;
			case 3:
				// 계좌내역보기
				break;
			case 4:
				delSaving(account_num);
				break;
			}
		}
	//적금 조회(전체 조회)
	public void printAll(Scanner sc) {
		System.out.println("적금 계좌 확인");
		ArrayList<Saving> list = dao.SelectAll(loginId);
		if(list.isEmpty()) {
			System.out.println("적금계좌가 존재하지 않습니다.");
		}else {
			for (Saving s : list) {
				if (MembersService.auth == false) {// 은행원이 아니면
					if (s.getId().equals(loginId)) {
						// 로그인한 아이디와 같은 계좌들만 print
						System.out.println(s);
					}
				} else {
					// 은행원이면 전부 프린트
					System.out.println(s);
				}
			}
		}
	}
	//입출금
	public void deposit(Scanner sc,Saving s,String account_num) {
		System.out.println("==입금==");
		System.out.println("얼마를 입금하시겠습니까?");
		int money = sc.nextInt();
		while (money < 0) {
			System.out.println("다시 입력해 주십시오.");
			money = sc.nextInt();
		};
		dao.update(s, money);
		System.out.println("입금이 완료되었습니다.");
	}
	//출금
	public void withdraw(Scanner sc,Saving s, String account_num) {
		System.out.println("==출금==");
		System.out.println("얼마를 출금하시겠습니까?");
		int money = sc.nextInt();
		while(money<0) {
			System.out.println("다시 입력해 주십시오.");
			money = sc.nextInt();
		}
		if (s.getBalance() < money) {
			System.out.println("계좌에 잔액이 부족합니다.");
		} else {
			dao.update(s,money*(-1));
			System.out.println("출금이 완료되었습니다.");
		}
	}
	
	
	//계좌 번호 조회
	public void selectSaving(Scanner sc) {
		System.out.println("계좌 확인");
		System.out.print("조회할 계좌 번호를 입력하세요:");
		String account_num = sc.next();
		//계좌의 주인일 때도 조회.
		Saving s = dao.selectByNum(account_num);
		if(s.equals(null)|| !s.getId().equals(loginId)) {
			System.out.println("조회 할 수 없는 계좌입니다.");
			return;
		}
		System.out.println(s);
	}
	//계좌 삭제
	public void delSaving(String num) {
		//계좌 번호 조회 후 금액이 0이면 삭제, 금액이 남으면 삭제 불가능
		System.out.println("===삭제===");
		if (dao.selectByNum(num).getBalance() == 0) {
			dao.delete(num);
			System.out.println("삭제가 완료되었습니다");
		} else {
			System.out.println("계좌에 잔액이 없어야 삭제할 수 있습니다.");
		}
	}
}